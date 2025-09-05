//package com.example.fruitapp
//
//import android.app.Activity
//import android.content.Intent
//import android.net.Uri
//import android.util.Log
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavHostController
//import coil.compose.rememberAsyncImagePainter
//import com.example.fruitapp.api.RetrofitClient
//import com.example.fruitapp.api.ApiService
//import com.example.fruitapp.api.UploadResponse
//import kotlinx.coroutines.launch
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.MultipartBody
//import okhttp3.RequestBody.Companion.toRequestBody
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun OpenAlbumScreen(navController: NavHostController, startUri: Uri?) {
//    // 從 navigation 的 savedStateHandle 中獲取圖片 URI
//    val savedImageUri = navController.previousBackStackEntry?.savedStateHandle?.get<Uri>("selectedImageUri")
//    var selectedUri by remember { mutableStateOf(savedImageUri ?: startUri) }
//    var isUploading by remember { mutableStateOf(false) }
//    var uploadResult by remember { mutableStateOf<String?>(null) }
//
//    val context = LocalContext.current
//    val apiService = RetrofitClient.apiService
//
//    // Snackbar state
//    val snackbarHostState = remember { SnackbarHostState() }
//    val scope = rememberCoroutineScope()
//
//    // 相簿 picker launcher
//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            selectedUri = result.data?.data
//        }
//    }
//
//    LaunchedEffect(savedImageUri) {
//        Log.d("AlbumScreen", "收到的 savedImageUri = $savedImageUri")
//        if (savedImageUri != null) {
//            selectedUri = savedImageUri
//        }
//    }
//
//    Scaffold(
//        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(24.dp)
//                .padding(padding),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            // 如果沒有選擇圖片，顯示選擇按鈕
//            if (selectedUri == null) {
//                Button(
//                    onClick = {
//                        launcher.launch(Intent(Intent.ACTION_PICK).apply { type = "image/*" })
//                    },
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text("選擇照片")
//                }
//            }
//
//            selectedUri?.let { uri ->
//                Image(
//                    painter = rememberAsyncImagePainter(uri),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(500.dp)
//                )
//
//                // 顯示上傳結果
//                uploadResult?.let { result ->
//                    Card(
//                        modifier = Modifier.fillMaxWidth(),
//                        colors = CardDefaults.cardColors(
//                            containerColor = MaterialTheme.colorScheme.primaryContainer
//                        )
//                    ) {
//                        Text(
//                            text = "辨識結果：$result",
//                            modifier = Modifier.padding(16.dp),
//                            style = MaterialTheme.typography.bodyLarge
//                        )
//                    }
//                }
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    Button(
//                        onClick = {
//                            // 上傳邏輯
//                            uploadImageWithSnackbar(
//                                uri = uri,
//                                apiService = apiService,
//                                context = context,
//                                onUploadStart = {
//                                    isUploading = true
//                                    uploadResult = null
//                                },
//                                onShowSnackbar = { message ->
//                                    scope.launch { snackbarHostState.showSnackbar(message) }
//                                },
//                                onUploadComplete = { result ->
//                                    isUploading = false
//                                    uploadResult = result
//                                }
//                            )
//                        },
//                        enabled = !isUploading
//                    ) {
//                        Text(if (isUploading) "辨識中..." else "辨識水果")
//                    }
//
//                    Button(onClick = {
//                        // 重新選擇照片
//                        uploadResult = null
//                        launcher.launch(Intent(Intent.ACTION_PICK).apply { type = "image/*" })
//                    }) {
//                        Text("重新選擇")
//                    }
//
//                    Button(onClick = {
//                        // 點擊取消時清除 savedStateHandle 並返回
//                        navController.previousBackStackEntry?.savedStateHandle?.remove<Uri>("selectedImageUri")
//                        navController.popBackStack()
//                    }) {
//                        Text("返回")
//                    }
//                }
//
//                // 如果有辨識結果，顯示確認按鈕
//                if (uploadResult != null) {
//                    Button(
//                        onClick = {
//                            navController.previousBackStackEntry?.savedStateHandle?.remove<Uri>("selectedImageUri")
//                            navController.popBackStack()
//                        },
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Text("確認結果")
//                    }
//                }
//            }
//        }
//    }
//}
//
//private fun uploadImageWithSnackbar(
//    uri: Uri,
//    apiService: ApiService,
//    context: android.content.Context,
//    onUploadStart: () -> Unit,
//    onShowSnackbar: (String) -> Unit,
//    onUploadComplete: (String) -> Unit
//) {
//    // 通知開始上傳
//    onUploadStart()
//    onShowSnackbar("🚀 上傳中...")
//
//    context.contentResolver.openInputStream(uri)?.use { stream ->
//        val bytes = stream.readBytes()
//        val reqBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
//        val part = MultipartBody.Part.createFormData("image", "upload.jpg", reqBody)
//
//        apiService.uploadImage(part).enqueue(object : Callback<UploadResponse> {
//            override fun onResponse(
//                call: Call<UploadResponse>,
//                response: Response<UploadResponse>
//            ) {
//                Log.d("API", "code=${response.code()}")
//                Log.d("API", "errorBody=${response.errorBody()?.string()}")
//                val result = response.body()?.result ?: "⚠️ 無回應訊息"
//                onShowSnackbar("✅ 辨識完成")
//                onUploadComplete(result)
//            }
//
//            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
//                val errorMsg = "❌ 上傳失敗：${t.message}"
//                onShowSnackbar(errorMsg)
//                onUploadComplete(errorMsg)
//            }
//        })
//    } ?: run {
//        val errorMsg = "❌ 無法讀取檔案"
//        onShowSnackbar(errorMsg)
//        onUploadComplete(errorMsg)
//    }
//}

package com.example.fruitapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.fruitapp.api.RetrofitClient
import com.example.fruitapp.api.ApiService
import com.example.fruitapp.api.UploadResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenAlbumScreen(navController: NavHostController,startUri: Uri?) {
    var selectedUri by remember { mutableStateOf(startUri) }  // 設定初始圖片為傳進來的 URI
    val context = LocalContext.current
    val apiService = RetrofitClient.apiService

    // Snackbar state
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 相簿 picker launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedUri = result.data?.data
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
//            Button(onClick = {
//                launcher.launch(Intent(Intent.ACTION_PICK).apply { type = "image/*" })
//            }) {
//                Text("選擇照片")
//            }//跳轉畫面將直接刪除

            selectedUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        // 上傳邏輯放在這裡，可以存取 snackbarHostState
                        uploadImageWithSnackbar(
                            uri = uri,
                            apiService = apiService,
                            context = context,
                            onShowSnackbar = { message ->
                                scope.launch { snackbarHostState.showSnackbar(message) }
                            }
                        )
                        // 上傳後清空預覽
                        selectedUri = null
                    }) {
                        Text("上傳")
                    }

                    Button(onClick = {
                        selectedUri = null
                    }) {
                        Text("取消")
                    }
                }
            }
        }
    }
}

private fun uploadImageWithSnackbar(
    uri: Uri,
    apiService: ApiService,
    context: android.content.Context,
    onShowSnackbar: (String) -> Unit
) {
    // 先通知使用者開始上傳
    onShowSnackbar("🚀 上傳中...")

    context.contentResolver.openInputStream(uri)?.use { stream ->
        val bytes = stream.readBytes()
        val reqBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("image", "upload.jpg", reqBody)

        apiService.uploadImage(part).enqueue(object : Callback<UploadResponse> {
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                Log.d("API", "code=${response.code()}")
                Log.d("API", "errorBody=${response.errorBody()?.string()}")
                val msg = response.body()?.result ?: "⚠️ 無回應訊息"
                onShowSnackbar(msg)
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                onShowSnackbar("❌ 上傳失敗：${t.message}")
            }
        })
    } ?: run {
        onShowSnackbar("❌ 無法讀取檔案")
    }
}