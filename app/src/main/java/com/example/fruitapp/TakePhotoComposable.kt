package com.example.fruitapp

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.view.Surface
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import com.example.fruitapp.api.RetrofitClient
import com.example.fruitapp.api.UploadResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executor

// Helper：依 EXIF orientation 旋轉 Bitmap
private fun rotateBitmapIfRequired(path: String, bitmap: Bitmap): Bitmap {
    val exif = ExifInterface(path)
    val orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )
    val matrix = Matrix().apply {
        postRotate(
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90   -> 90f
                ExifInterface.ORIENTATION_ROTATE_180  -> 180f
                ExifInterface.ORIENTATION_ROTATE_270  -> 270f
                else                                  -> 0f
            }
        )
    }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TakePhotoScreen(viewModel: RecordViewModel) {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    val executor: Executor = ContextCompat.getMainExecutor(context)
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Scaffold + SnackbarHostState
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 相機權限
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            scope.launch { snackbarHostState.showSnackbar("❌ 沒有攝影機權限") }
        }
    }
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (capturedBitmap == null) {
                // 相機預覽
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val wm = ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                        val rotation = wm.defaultDisplay?.rotation ?: Surface.ROTATION_0

                        val preview = Preview.Builder()
                            .setTargetRotation(rotation)
                            .build()
                            .also { it.setSurfaceProvider(previewView.surfaceProvider) }

                        imageCapture = ImageCapture.Builder()
                            .setTargetRotation(rotation)
                            .build()

                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                ctx as ComponentActivity,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                imageCapture
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        previewView
                    }
                )

                // 拍照按鈕
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 32.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Button(
                        onClick = {
                            val file = File.createTempFile("photo_", ".jpg", context.cacheDir)
                            val options = ImageCapture.OutputFileOptions.Builder(file).build()
                            imageCapture?.takePicture(
                                options,
                                executor,
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                        val rawBmp = BitmapFactory.decodeFile(file.absolutePath)
                                        val fixedBmp = rotateBitmapIfRequired(file.absolutePath, rawBmp)
                                        capturedBitmap = fixedBmp
                                    }
                                    override fun onError(e: ImageCaptureException) {
                                        scope.launch { snackbarHostState.showSnackbar("❌ 拍照錯誤") }
                                    }
                                }
                            )
                        },
                        modifier = Modifier
                            .height(64.dp)
                            .width(200.dp)
                    ) {
                        Text("📸 拍照", style = MaterialTheme.typography.titleMedium)
                    }
                }

            } else {
                // 顯示照片
                Image(
                    bitmap = capturedBitmap!!.asImageBitmap(),
                    contentDescription = "已拍攝照片",
                    modifier = Modifier.fillMaxSize()
                )
                // 上傳 / 重拍
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 32.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(onClick = {
                            val file = File.createTempFile("upload_", ".jpg", context.cacheDir)
                            FileOutputStream(file).use {
                                capturedBitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, it)
                            }
                            val req = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                            val part = MultipartBody.Part.createFormData("image", file.name, req)
                            scope.launch { snackbarHostState.showSnackbar("🚀 偵測中...") }
                            RetrofitClient.apiService.uploadImage(part)
                                .enqueue(object : Callback<UploadResponse> {
                                    override fun onResponse(
                                        call: Call<UploadResponse>,
                                        response: Response<UploadResponse>
                                    ) {
                                        val message = response.body()?.result ?: "⚠️ 沒有回傳內容"
                                        scope.launch { snackbarHostState.showSnackbar(message) }
                                        viewModel.addRecord(message)
                                    }
                                    override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                                        scope.launch { snackbarHostState.showSnackbar("❌ 上傳失敗") }
                                    }
                                })
                        }) {
                            Text("✅ 使用這張")
                        }
                        Button(onClick = { capturedBitmap = null }) {
                            Text("🔁 重新拍攝")
                        }
                    }
                }
            }
        }
    }
}
