package com.example.fruitapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
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

    AndroidView(factory = {
        VideoView(it).apply {
            setVideoURI(Uri.parse("android.resource://${context.packageName}/${R.raw.app_animation}"))
            setOnCompletionListener {
                onFinished()
            }
            start()
        }
    })
}
