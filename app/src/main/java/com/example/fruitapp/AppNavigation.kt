package com.example.fruitapp

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


// --- 佔位畫面 ---
@Composable
fun HomeScreenContent(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        HomeScreen(navController = navController)
    }
}

@Composable
fun MapScreenContent(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        MapScreen(navController = navController)
    }
}

@Composable
fun AppNavigation(
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("camera") {
            TakePhotoScreen()
        }
        composable("album") { backStackEntry ->
            val selectedImageUri = backStackEntry.savedStateHandle.get<Uri>("selectedImageUri")
            OpenAlbumScreen(navController, selectedImageUri)
        }
        composable("map") {
            MapScreen(navController = navController)
        }
        composable("settings") {
            SettingsScreen(navController = navController)
        }
        composable("version_info") {
            VersionInfoScreen(navController = navController)
        }
        composable("history") {
            HistoryScreen(navController = navController)
        }
        composable("user_settings") {
            UserSettingsScreen(
                navController = navController,
                isDarkMode = isDarkMode,
                onThemeChange = onThemeChange
            )
        }
    }
}


