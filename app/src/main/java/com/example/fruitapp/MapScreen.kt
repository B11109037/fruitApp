@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.fruitapp
import android.content.Context
import android.os.Looper
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.GoogleMap
// 網路請求（OkHttp）
import okhttp3.OkHttpClient
import okhttp3.Request
// JSON 處理
import org.json.JSONObject

import androidx.compose.runtime.rememberCoroutineScope
import com.example.fruitapp.network.RetrofitInstance
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


@Composable
fun MapScreen() {
    //目前的位置
    val context = LocalContext.current
    //取得定位服務
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    //權限狀態
    val locationPermission = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    //記住目前位置
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    //方便直接使用camera
    val coroutineScope = rememberCoroutineScope()

    var searchResults by remember { mutableStateOf<List<Pair<String, LatLng>>>(emptyList()) }

    val apiKey = "AIzaSyAuEoMZPDV9xWY1F7-ghm_xYG9X-uvhpWc"
    // 請求定位權限
    LaunchedEffect(Unit) {
        locationPermission.launchPermissionRequest()
    }

    // ✅ 改用即時定位 requestLocationUpdates（避免 lastLocation 為 null）
    LaunchedEffect(locationPermission.status) {
        if (locationPermission.status.isGranted) {
            //高精準度GPS每1秒確認一次位置更新
            val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
                interval = 0   // 不需要重複更新
                numUpdates = 1 //  只要一次更新
            }
            //if回傳位置就會觸發callback
            val locationCallback = object : com.google.android.gms.location.LocationCallback() {
                override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                    result.lastLocation?.let {
                        currentLocation = LatLng(it.latitude, it.longitude)
                        Log.d("MapScreen", "✅ 即時定位取得: ${it.latitude}, ${it.longitude}")
                    } ?: Log.e("MapScreen", "❌ 無法取得位置（可能尚未設定 GPS 模擬定位）")
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    // 建立地圖相機
    val cameraPositionState = rememberCameraPositionState()
    //讓相機移動到GPS定位初始位置
    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(it, 16f),
                1000
            )
        }
    }
    // 使用 Text Search API 搜尋附近水果店
    LaunchedEffect(currentLocation) {
        currentLocation?.let { location ->
            coroutineScope.launch {
                try {
                    val response = RetrofitInstance.api.searchPlaces(
                        query = "水果",
                        location = "${location.latitude},${location.longitude}",
                        radius = 500, // 調整搜尋範圍（公尺）
                        apiKey = apiKey
                    )
                    searchResults = response.results.map {
                        it.name to LatLng(it.geometry.location.lat, it.geometry.location.lng)
                    }
                    Log.d("TextSearch", "✅ 找到 ${searchResults.size} 間水果店")
                } catch (e: Exception) {
                    Log.e("TextSearch", "❌ 搜尋錯誤: ${e.message}")
                }
            }
        }
    }



    // 顯示地圖 + 使用者位置 Marker
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        currentLocation?.let {
            Log.d("MapScreen", "地圖顯示標記位置: ${it.latitude}, ${it.longitude}")
            Marker(
                state = MarkerState(position = it),
                title = "你在這裡"
            )
        }
        // 顯示搜尋結果
        searchResults.forEach { (name, latLng) ->
            Marker(
                state = MarkerState(position = latLng),
                title = name
            )
        }
    }
}

//// 👉 搜尋附近水果地點的函式
//suspend fun searchNearbyFruitShops(
//    context: Context,
//    location: LatLng,
//    placesClient: PlacesClient
//): List<Pair<String, LatLng>> {
//    val request = FindAutocompletePredictionsRequest.builder()
//        .setQuery("fruit store")
//        .setLocationBias(
//            RectangularBounds.newInstance(
//                //設定附近範圍附近300公尺
//                LatLng(location.latitude - 0.003, location.longitude - 0.003),
//                LatLng(location.latitude + 0.003, location.longitude + 0.003)
//            )
//        )
//        .build()
//
//    return try {
//        val result = placesClient.findAutocompletePredictions(request).await()
//        result.autocompletePredictions.mapNotNull { prediction ->
//            val placeId = prediction.placeId
//            val name = prediction.getPrimaryText(null).toString()
//
//            val placeRequest = FetchPlaceRequest.builder(placeId, listOf(Place.Field.LAT_LNG)).build()
//            val placeResponse = placesClient.fetchPlace(placeRequest).await()
//            val latLng = placeResponse.place.latLng
//            if (latLng != null) name to latLng else null
//        }
//    } catch (e: Exception) {
//        Log.e("PlacesSearch", "❌ 搜尋失敗: ${e.message}")
//        emptyList()
//    }
//}