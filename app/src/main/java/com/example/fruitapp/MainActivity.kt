package com.example.fruitapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.fruitapp.ui.theme.FruitAppTheme
import com.google.android.libraries.places.api.Places

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FruitAppTheme {
                // 初始化 Places
                if (!Places.isInitialized()) {
                    Places.initialize(applicationContext, "AIzaSyAuEoMZPDV9xWY1F7-ghm_xYG9X-uvhpWc")
                }
                AppNavigation()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FruitAppTheme {
        AppNavigation()
    }
}