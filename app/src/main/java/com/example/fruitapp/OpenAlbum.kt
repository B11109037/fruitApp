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
fun OpenAlbumScreen() {
    val context = LocalContext.current
    val apiService = RetrofitClient.apiService
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

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
            Button(onClick = {
                launcher.launch(Intent(Intent.ACTION_PICK).apply { type = "image/*" })
            }) {
                Text("選擇照片")
            }

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
