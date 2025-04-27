package com.example.fruitapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch


//@Composable
//fun HomeScreen(navController: NavHostController) {
//    Scaffold(
//        bottomBar = { MyNavigationBar() }
//    ) { innerPadding ->
//        Column(
//            modifier = Modifier
//                .padding(innerPadding)
//                .padding(16.dp)
//        ) {
//            Text("Content for the Home Screen goes here")
//        }
//    }
//    TakePhotoScreen()
//}
//
//@Composable
//fun MyNavigationBar() {
//    var selectedItem by remember { mutableIntStateOf(0) }
//    val items = listOf("Home", "Profile", "Settings")
//    val icons = listOf(Icons.Filled.Home, Icons.Filled.Person, Icons.Filled.Settings)
//
//    NavigationBar {
//        items.forEachIndexed { index, item ->
//            NavigationBarItem(
//                icon = { Icon(icons[index], contentDescription = item) },
//                label = { Text(item) },
//                selected = selectedItem == index,
//                onClick = { selectedItem = index }
//            )
//        }
//    }
//}

data class DrawerItem(
    val title: String,
    val icon: ImageVector,
    val route: String // 將會用來處理導覽邏輯
)

val drawerItems = listOf(
    DrawerItem(
        title = "Home",
        icon = Icons.Filled.Home,
        route = "home"
    ),
    DrawerItem(
        title = "Profile",
        icon = Icons.Filled.Person,
        route = "profile"
    ),
    DrawerItem(
        title = "Settings",
        icon = Icons.Filled.Settings,
        route = "settings"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedRoute by remember { mutableStateOf("home") } // 預設為 "home" 頁面

    // 最外層使用 ModalNavigationDrawer 以確保抽屜正確顯示
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // 添加一個抽屜標題
                Text(
                    text = "水果辨識應用",
                    modifier = Modifier.padding(16.dp),
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge
                )
                
                // 分隔線
                androidx.compose.material3.Divider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                // 抽屜項目
                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(text = item.title) },
                        icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                        selected = selectedRoute == item.route,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                selectedRoute = item.route
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    ) {
        // 使用 Scaffold 作為主要容器
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            when (selectedRoute) {
                                "home" -> "水果辨識"
                                "profile" -> "個人資料"
                                "settings" -> "設定"
                                else -> "水果辨識應用"
                            }
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = "開啟選單")
                        }
                    }
                )
            },
            // 確保 Scaffold 背景是透明的
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) { innerPadding ->
            // 內容區域
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedRoute) {
                    "home" -> TakePhotoScreen() // 在內容區域直接顯示拍照畫面
                    "profile" -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        ProfileScreenContent()
                    }
                    "settings" -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        SettingsScreenContent()
                    }
                }
            }
        }
    }
}

// 簡化內容頁面組件，移除無需的 innerPadding 參數
@Composable
fun ProfileScreenContent() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(
            "個人資料",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("此處可以顯示用戶個人資料和水果辨識歷史記錄")
    }
}

@Composable
fun SettingsScreenContent() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(
            "設定",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("此處可以進行應用程式的相關設定")
    }
}