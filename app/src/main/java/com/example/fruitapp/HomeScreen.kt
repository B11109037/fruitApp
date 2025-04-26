package com.example.fruitapp

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import java.io.File

@Composable
fun HomeScreen(navController: NavHostController) {
    TakePhotoScreen()
}
//    val context = LocalContext.current
//    val lifecycleOwner = LocalLifecycleOwner.current
//    val imageCapture = remember { ImageCapture.Builder().build() }
//
//    Column(modifier = Modifier.fillMaxSize()) {
//        // Camera 預覽畫面
//        CameraPreviewView(
//            context = context,
//            lifecycleOwner = lifecycleOwner,
//            imageCapture = imageCapture
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // 拍照按鈕
//        Button(
//            onClick = {
//                val photoFile = File(context.cacheDir, "guava_${System.currentTimeMillis()}.jpg")
//                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
//
//                imageCapture.takePicture(
//                    outputOptions,
//                    ContextCompat.getMainExecutor(context),
//                    object : ImageCapture.OnImageSavedCallback {
//                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
//                            Toast.makeText(context, "✅ 拍照成功", Toast.LENGTH_SHORT).show()
//                            // TODO: 呼叫 API 傳送照片
//                        }
//
//                        override fun onError(exception: ImageCaptureException) {
//                            Toast.makeText(context, "❌ 拍照失敗", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                )
//            },
//            modifier = Modifier.align(Alignment.CenterHorizontally)
//        ) {
//            Text("拍照並傳送")
//        }
//    }
//}
//
//@Composable
//fun CameraPreviewView(
//    context: Context,
//    lifecycleOwner: LifecycleOwner,
//    imageCapture: ImageCapture
//) {
//    val previewView = remember { PreviewView(context) }
//
//    AndroidView(factory = { previewView }) {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
//        cameraProviderFuture.addListener({
//            val cameraProvider = cameraProviderFuture.get()
//
//            val preview = Preview.Builder().build().also {
//                it.setSurfaceProvider(previewView.surfaceProvider)
//            }
//
//            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//
//            try {
//                cameraProvider.unbindAll()
//                cameraProvider.bindToLifecycle(
//                    lifecycleOwner,
//                    cameraSelector,
//                    preview,
//                    imageCapture
//                )
//            } catch (exc: Exception) {
//                Log.e("CameraX", "Camera binding failed", exc)
//            }
//        }, ContextCompat.getMainExecutor(context))
//    }
//}
//
