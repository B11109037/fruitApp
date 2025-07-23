package com.example.fruitapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

// --------- HomeScreen ---------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    // 使用 Scaffold 作為主要容器
    Scaffold(

        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                   .background(Color.White), // 或你要的背景色
                contentAlignment = Alignment.Center
            ) {
                // 中間標題
                Text(
                    text = "果然會辨識",
                    style = MaterialTheme.typography.titleMedium
                )

                // 左右 Icon（定位與設定）
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.navigate("map") }) {
                        Icon(Icons.Default.LocationOn, contentDescription = "地圖")
                    }
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "設定")
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
            // 中央拍照邊框
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-150).dp) // 🡄 往上移動 48dp
                    .size(160.dp)
                    .border(
                        width = 4.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(24.dp)
                    )
            )
            TakePhotoScreen()
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(250.dp)
//                    .align(Alignment.BottomCenter)
//                    .background(Color.Black.copy(alpha = 0.7f))
//
//            ) {
//                // 底部控制區塊
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 32.dp),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    // 左側相簿按鈕
//                    IconButton(
//                        onClick = { /* TODO: 開啟相簿 */ },
//                        modifier = Modifier
//                            .padding(top = 20.dp)
//                            .size(56.dp)
//                            .clip(CircleShape)
//                            .background(Color.White)
//                    ) {
//                        Icon(
//                            Icons.Default.Settings, contentDescription = "設定"
//                        )
//                    }
////                    // 中間拍照按鈕（額外用 Box 包起來讓它置中）
////                    Box(
////                        modifier = Modifier
////                            .fillMaxWidth()
////                            .padding(top = 20.dp),
////                        contentAlignment = Alignment.Center
////                    ) {
////                        IconButton(
////                            onClick = { /* TODO: 拍照功能 */ },
////                            modifier = Modifier
////
////                                .size(72.dp)
////                                .clip(CircleShape)
////                                .background(Color.White)
////                        ) {
////                            Icon(
////                                Icons.Default.Settings, contentDescription = "設定"
////                            )
////                        }
////                    }
//                }
//            }


        }
    }
}


