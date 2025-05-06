package com.example.fruitapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.fruitapp.api.RetrofitClient
import com.example.fruitapp.api.ApiService
import com.example.fruitapp.api.UploadResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun OpenAlbumScreen() {
    val context = LocalContext.current
    val apiService = RetrofitClient.apiService
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedUri = result.data?.data
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = {
            val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
            launcher.launch(intent)
        }) {
            Text("選擇照片")
        }

        selectedUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = {
                    uploadImage(context, uri, apiService)
                    selectedUri = null
                }) {
                    Text("上傳")
                }
                Button(onClick = { selectedUri = null }) {
                    Text("取消")
                }
            }
        }
    }
}

fun uploadImage(context: Context, uri: Uri, apiService: ApiService) {
    val inputStream = context.contentResolver.openInputStream(uri)

    inputStream?.use { stream ->
        val bytes = stream.readBytes()
        val reqBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("photo", "upload.jpg", reqBody)

        apiService.uploadImage(part).enqueue(object : Callback<UploadResponse> {
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                val msg = response.body()?.result ?: "無回應訊息"
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                Toast.makeText(context, "上傳失敗：${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
