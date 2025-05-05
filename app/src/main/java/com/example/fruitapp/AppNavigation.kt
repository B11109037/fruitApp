package com.example.fruitapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController


data class AppNavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: String // 將會用來處理導覽邏輯
)
val appNavigationItems = listOf(
    AppNavigationItem(
        title = "Home",
        icon = Icons.Filled.Home,
        route = "home" // 保持 home
    ),
    AppNavigationItem(
        title = "map",
        icon = Icons.Filled.LocationOn,
        route = "map" // 將 profile 改為 map
    ),
    AppNavigationItem(
        title = "album",
        icon = Icons.Filled.PlayArrow,
        route = "album" // 將 settings 改為 album
    )
)

// --- 佔位畫面 ---
@Composable
fun HomeScreenContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        HomeScreen(navController = rememberNavController())
        // 之後可以替換成 TakePhotoScreen() 或其他主畫面內容
    }
}

@Composable
fun MapScreenContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        MapScreen()
        // 之後可以替換成實際的地圖畫面
    }
}

@Composable
fun AlbumScreenContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        OpenAlbumScreen()
    }
}

@Composable
fun AppNavigation() {
    var selectedRoute by rememberSaveable { mutableStateOf("home") }

    Scaffold(
        bottomBar = {
            BottomBar(
                selectedRoute = selectedRoute,
                onItemSelected = { selectedRoute = it }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            when (selectedRoute) {
                "home" -> HomeScreenContent() // 可內嵌 TakePhotoScreen()
                "map" -> MapScreenContent()
                "album" -> AlbumScreenContent()
            }
        }
    }
}

@Composable
fun BottomBar(
    selectedRoute: String,
    onItemSelected: (String) -> Unit
) {
    BottomAppBar {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            appNavigationItems.forEach { item ->
                val selected = selectedRoute == item.route
                IconButton(onClick = {
                    onItemSelected(item.route)
                }) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

