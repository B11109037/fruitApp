package com.example.fruitapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

//@Composable
//fun AppNavigation() {
//    val navController = rememberNavController()
//
//    NavHost(navController = navController, startDestination = "home") {
//        composable(route = "home") {
//            HomeScreen(navController = navController)
//        }
//        composable(route = "map") {
//            MapScreen()
//        }
//    }
//}


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    //浮現螢幕上不會消失
    Scaffold(
        bottomBar = {
            BottomBar(navController)
        }
    ) { innerPadding ->//內容
        //控制器主持人
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(navController) }
            composable("map") { MapScreen() }
        }
    }
}

//固定底部導覽列
@Composable
fun BottomBar(navController: NavHostController) {
    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { navController.navigate("home") }) {
                Text("Home")
            }
            Button(onClick = { navController.navigate("map") }) {
                Text("Map")
            }
        }
    }
}

