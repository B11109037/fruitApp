package com.example.fruitapp

import android.Manifest
import android.graphics.Bitmap
import android.widget.Toast
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
import com.example.fruitapp.api.RetrofitClient
import com.example.fruitapp.api.UploadResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executor

@Composable
fun TakePhotoScreen() {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    val executor: Executor = ContextCompat.getMainExecutor(context)

    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(context, "❌ 沒有攝影機權限", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // 使用 Box 包裹所有內容，確保它們能正確顯示
    Box(modifier = Modifier.fillMaxSize()) {
        if (capturedBitmap == null) {
            // 相機預覽作為底層
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

            //黑底層
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.7f))

            ) {
                // 拍照按鈕放在底部中央
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 100.dp),
                    contentAlignment = androidx.compose.ui.Alignment.BottomCenter
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
                                            val bitmap = android.graphics.BitmapFactory.decodeFile(file.absolutePath)
                                            capturedBitmap = bitmap
                                        }

                                        override fun onError(e: ImageCaptureException) {
                                            Toast.makeText(context, "❌ 拍照錯誤", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                ) },
                            modifier = Modifier

                                .size(90.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        ) {
                        }
                }
            }

        } else {
            // 顯示已拍攝的照片
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

                // 底部按鈕
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 32.dp),
                    contentAlignment = androidx.compose.ui.Alignment.BottomCenter
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(onClick = {
                            val file = File.createTempFile("upload_", ".jpg", context.cacheDir)
                            FileOutputStream(file).use {
                                capturedBitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, it)
                            }

                            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                            val multipart = MultipartBody.Part.createFormData("image", file.name, requestFile)

                            Toast.makeText(context, "🚀 偵測中...", Toast.LENGTH_SHORT).show()

                            RetrofitClient.apiService.uploadImage(multipart)
                                .enqueue(object : Callback<UploadResponse> {
                                    override fun onResponse(
                                        call: Call<UploadResponse>,
                                        response: Response<UploadResponse>
                                    ) {
                                        val result = response.body()?.result ?: "⚠️ 沒有回傳內容"
                                        Toast.makeText(context, result, Toast.LENGTH_LONG).show()
                                    }

                                    override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                                        Toast.makeText(context, "❌ 上傳失敗", Toast.LENGTH_SHORT).show()
                                    }
                                })
                        } ,
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

                        Button(onClick = { capturedBitmap = null },
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
