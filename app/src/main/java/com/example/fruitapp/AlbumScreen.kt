package com.example.fruitapp

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.example.fruitapp.api.RetrofitClient
import com.example.fruitapp.api.UploadResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

@Composable
fun AlbumScreen() {
    val context = LocalContext.current
    val cacheDir = context.cacheDir
    var selectedImage by remember { mutableStateOf<File?>(null) }
    
    // 獲取緩存中的所有圖片文件
    val imageFiles = remember {
        cacheDir.listFiles { file ->
            file.isFile && (file.name.endsWith(".jpg") || file.name.endsWith(".jpeg") || file.name.endsWith(".png"))
        }?.toList() ?: emptyList()
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        if (imageFiles.isEmpty()) {
            // 如果沒有圖片，顯示提示訊息
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("沒有圖片可顯示，請先拍攝照片！")
            }
        } else {
            // 如果有圖片，顯示網格
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(imageFiles) { file ->
                    PhotoItem(file = file, onClick = { selectedImage = file })
                }
            }
        }
        
        // 顯示選中的圖片
        if (selectedImage != null) {
            Dialog(onDismissRequest = { selectedImage = null }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(450.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(Uri.fromFile(selectedImage)),
                            contentDescription = "Selected photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "檔案名稱: ${selectedImage?.name}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // 新增確認和取消按鈕
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            androidx.compose.material3.Button(
                                onClick = {
                                    // 處理傳送圖片到伺服器
                                    selectedImage?.let { file ->
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
                                                    selectedImage = null
                                                }
                                                
                                                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                                                    Toast.makeText(context, "❌ 上傳失敗", Toast.LENGTH_SHORT).show()
                                                    selectedImage = null
                                                }
                                            })
                                    }
                                }
                            ) {
                                Text("✅ 使用此照片")
                            }
                            
                            androidx.compose.material3.Button(
                                onClick = { selectedImage = null },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            ) {
                                Text("❌ 取消")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PhotoItem(file: File, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = rememberAsyncImagePainter(Uri.fromFile(file)),
            contentDescription = "Photo",
            modifier = Modifier.fillMaxSize()
        )
    }
}
