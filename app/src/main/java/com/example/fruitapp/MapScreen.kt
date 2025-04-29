package com.example.fruitapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen() {
    // 定義台灣中央位置 (約略位置)
    val taiwanPosition = LatLng(23.7, 121.0)
    
    // 記住相機位置狀態
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(taiwanPosition, 7f)
    }
    
    // 如果想要顯示真實地圖，使用這個
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        // 在台北市新增一個標記
        Marker(
            state = MarkerState(position = LatLng(25.033, 121.565)),
            title = "台北市",
            snippet = "台灣首都"
        )
        
        // 在高雄市新增一個標記
        Marker(
            state = MarkerState(position = LatLng(22.633, 120.266)),
            title = "高雄市",
            snippet = "港都"
        )
    }
    
    // 如果無法顯示地圖，則顯示這個替代內容
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "地圖加載中...\n如果長時間無法顯示，請確認是否已經設定 Google Maps API Key",
            textAlign = TextAlign.Center
        )
    }
}