package com.example.fruitapp

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// --------- HomeScreen ---------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
//    val drawerState = rememberDrawerState(DrawerValue.Closed)
//    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    // 建立圖片 launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri = result.data?.data
            selectedImageUri?.let {
                // 將選擇的圖片 URI 存到 navController 的 SavedState
                navController.currentBackStackEntry?.savedStateHandle?.set("selectedImageUri", it)
                navController.navigate("album") {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }


    // 使用 Scaffold 作為主要容器
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(Color.Gray.copy(alpha = 0.6f))// 灰色且透明, // 或你要的背景色
                    ,//如果要往下移可以padding
                contentAlignment = Alignment.Center
            ) {
                // 中間標題
                Text(
                    text = "果然會辨識",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize=28.sp
                    ),
                    modifier = Modifier.padding(top = 30.dp)
                )

                // 左右 Icon（定位與設定）
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(top = 30.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.navigate("map") }) {
                        Icon(Icons.Default.LocationOn, contentDescription = "地圖", modifier = Modifier.size(32.dp))

                    }
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "設定",modifier = Modifier.size(32.dp))
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) {innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TakePhotoScreen()
            // 中央拍照邊框
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-120).dp) //  往上移動
                    .size(160.dp)
                    .border(
                        width = 4.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(24.dp)
                    )
            )
            //圖庫
            IconButton(
                onClick = {
                            launcher.launch(Intent(Intent.ACTION_PICK).apply {
                                type = "image/*"
                            })
                          },
                modifier = Modifier
                    .align(Alignment.BottomCenter) // 先置中
                    .offset(x = (-120).dp, y = (-135).dp) // 往左移、微微往上
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Icon(
                    Icons.Default.Add, contentDescription = "相簿"
                )
            }
        }
    }
}