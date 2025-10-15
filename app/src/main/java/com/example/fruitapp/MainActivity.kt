package com.example.fruitapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.fruitapp.ui.theme.FruitAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()

            // 從 DataStore 讀取深色模式設定
            val isDarkMode by UserPreferences.getDarkModeFlow(context).collectAsState(initial = false)

            // 切換主題時，同步寫入 DataStore
            val onThemeChange: (Boolean) -> Unit = { enabled ->
                scope.launch {
                    UserPreferences.setDarkMode(context, enabled)
                }
            }

            // 使用 DataStore 的值控制主題
            FruitAppTheme(darkTheme = isDarkMode) {
                AppNavigation(
                    isDarkMode = isDarkMode,
                    onThemeChange = onThemeChange
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