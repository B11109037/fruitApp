package com.example.fruitapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.fruitapp.ui.theme.FruitAppTheme

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FruitAppTheme {
                VideoSplashScreen {
                    // 動畫播放完畢，切換至主畫面
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}


@Composable
fun VideoSplashScreen(onFinished: () -> Unit) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // 讓背景乾淨一點
        contentAlignment = Alignment.TopCenter // 顯示位置對齊
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp), // 控制往下移
            factory = {
                VideoView(it).apply {
                    setVideoURI(Uri.parse("android.resource://${context.packageName}/${R.raw.app_animation}"))
                    setOnCompletionListener {
                        onFinished()
                    }
                    start()
                }
            }
        )
    }
}
//@Composable
//fun VideoSplashScreen(onFinished: () -> Unit) {
//    val context = LocalContext.current
//
//    AndroidView(factory = {
//        VideoView(it).apply {
//            setVideoURI(Uri.parse("android.resource://${context.packageName}/${R.raw.app_animation}"))
//            setOnCompletionListener {
//                onFinished()
//            }
//            start()
//        }
//    })
//}
