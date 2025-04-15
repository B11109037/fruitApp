package com.example.fruitapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.fruitapp.ui.theme.FruitAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FruitAppTheme {
                AppNavigation()
            }
        }
    }
}

//@Composable
//fun App(modifier: Modifier = Modifier){
//    Scaffold(
//        bottomBar = { BottomBar() }
//    ){ contentPadding ->
//
//    }
//
//}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FruitAppTheme {
        AppNavigation()
    }
}