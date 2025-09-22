// MainActivity.kt
package com.example.fruitapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.fruitapp.ui.theme.FruitAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // 在這裡定義深色模式的狀態
            var isDarkMode by remember { mutableStateOf(false) }

            // 將狀態傳遞給 FruitAppTheme
            FruitAppTheme(darkTheme = isDarkMode) {
                AppNavigation(
                    isDarkMode = isDarkMode,
                    onThemeChange = { isDarkMode = it }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    // 傳入預覽所需的參數
    FruitAppTheme {
        AppNavigation(
            isDarkMode = false, // 預覽時設定為深色模式關閉
            onThemeChange = {}  // 傳入一個空的函式，因為預覽不需要實際改變狀態
        )
    }
}