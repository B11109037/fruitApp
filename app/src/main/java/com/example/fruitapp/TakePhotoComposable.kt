package com.example.fruitapp

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import com.example.fruitapp.api.RetrofitClient
import com.example.fruitapp.api.UploadResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executor

// 新增函數：根據EXIF數據旋轉圖片
fun rotateBitmap(bitmap: Bitmap, filePath: String): Bitmap {
    val exif = ExifInterface(filePath)
    val orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )

    val matrix = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        else -> return bitmap
    }

    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

@Composable
fun TakePhotoScreen() {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    val executor: Executor = ContextCompat.getMainExecutor(context)

    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("❌ 沒有攝影機權限")
            }
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // 使用 Scaffold 包裹內容以支援 Snackbar
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (capturedBitmap == null) {
                // ======== 預覽與拍照區 ========
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        val previewView = androidx.camera.view.PreviewView(ctx)
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        imageCapture = ImageCapture.Builder().build()
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                ctx as ComponentActivity,
                                cameraSelector,
                                preview,
                                imageCapture
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        previewView
                    }
                )

                // 黑色底層與拍照按鈕
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.7f))
                ) {
                    // 拍照按鈕
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 100.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        IconButton(
                            onClick = {
                                val file = File.createTempFile("guava_", ".jpg", context.cacheDir)
                                val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

                                imageCapture?.takePicture(
                                    outputOptions,
                                    executor,
                                    object : ImageCapture.OnImageSavedCallback {
                                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                            val bitmap =
                                                android.graphics.BitmapFactory.decodeFile(file.absolutePath)
                                            // 旋轉圖片到正確方向
                                            capturedBitmap = rotateBitmap(bitmap, file.absolutePath)
                                        }

                                        override fun onError(e: ImageCaptureException) {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("❌ 拍照錯誤")
                                            }
                                        }
                                    }
                                )
                            },
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        ) {
                            // 你可以放個圖示或加個圈
                        }
                    }
                }
            } else {
                // ======== 顯示已拍照片與底部按鈕區 ========
                Image(
                    bitmap = capturedBitmap!!.asImageBitmap(),
                    contentDescription = "已拍攝照片",
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.7f))
                ) {
                    // 底部操作按鈕
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 32.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // 確認使用按鈕
                            Button(
                                onClick = {
                                    val file =
                                        File.createTempFile("upload_", ".jpg", context.cacheDir)
                                    FileOutputStream(file).use {
                                        capturedBitmap!!.compress(
                                            Bitmap.CompressFormat.JPEG,
                                            90,
                                            it
                                        )
                                    }

                                    val requestFile =
                                        file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                                    val multipart = MultipartBody.Part.createFormData(
                                        "image",
                                        file.name,
                                        requestFile
                                    )

                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("🚀 偵測中...")
                                    }

                                    RetrofitClient.apiService.uploadImage(multipart)
                                        .enqueue(object : Callback<UploadResponse> {
                                            override fun onResponse(
                                                call: Call<UploadResponse>,
                                                response: Response<UploadResponse>
                                            ) {
                                                val result =
                                                    response.body()?.result ?: "⚠️ 沒有回傳內容"
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar(result)
                                                    withContext(Dispatchers.IO) {
                                                        val database = AppDatabase.getInstance(context)
                                                        val dao = database.recordDao()
                                                        dao.insert(
                                                            Record(
                                                                timestamp = System.currentTimeMillis(),
                                                                message = result
                                                            )
                                                        )
                                                    }
                                                }
                                            }

                                            override fun onFailure(
                                                call: Call<UploadResponse>,
                                                t: Throwable
                                            ) {
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("❌ 上傳失敗")
                                                }
                                            }
                                        })
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "使用",
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                                Text("使用")
                            }

                            // 重新拍攝按鈕
                            Button(
                                onClick = { capturedBitmap = null },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "重新拍攝",
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                                Text("重新拍攝")
                            }
                        }
                    }
                }
            }
        }
    }
}
