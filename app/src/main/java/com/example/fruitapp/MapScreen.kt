@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.fruitapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.net.URL
import android.location.Location


@Composable
fun MapScreen(navController: NavHostController) {
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


    // 可調參數：關鍵字與半徑
    var query by remember { mutableStateOf("水果") }
    var radiusMeters by remember { mutableStateOf(2000.0) } // 公尺（Double）

    data class ResultItem(val name: String, val latLng: LatLng, val address: String?)

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
            //檢查是否有權限，如果沒有要catch
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                try {
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )
                } catch (e: SecurityException) {
                    Log.e("MapScreen", "❌ 權限不足，無法啟動定位: ${e.message}")
                }
            } else {
                Log.w("MapScreen", "⚠️ 尚未取得定位權限")
            }
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

    LaunchedEffect(currentLocation, query, radiusMeters) {
        val loc = currentLocation ?: return@LaunchedEffect
        coroutineScope.launch {
            try {
                // ① searchText 正確用法：locationBias（不是 locationRestriction）
                val request = NewPlacesRequest(
                    textQuery = query,
                    locationBias = LocationBias(
                        circle = Circle(
                            center = LatLngLiteral(loc.latitude, loc.longitude),
                            radius = radiusMeters              // 影響排序；硬限制改由客端過濾
                        )
                    ),
                    // 你若有 maxResultCount 就放在 request 裡（若 data class 有這欄位）
                    // maxResultCount = 50
                )

                val resp = RetrofitInstance.newPlacesApi.searchPlaces(
                    request = request,
                    apiKey = apiKey,
                    fieldMask = "places.id,places.displayName,places.location,places.formattedAddress,places.rating,places.types"
                )

                if (resp.isSuccessful) {
                    val body = resp.body()

                    // ② 客端硬限制：把超過半徑的結果過濾掉
                    val list = body?.places.orEmpty()
                        .mapNotNull { p ->
                            val name = p.displayName?.text
                            val lat  = p.location?.latitude
                            val lng  = p.location?.longitude
                            if (name != null && lat != null && lng != null) {
                                name to LatLng(lat, lng)
                            } else null
                        }
                        .filter { (_, point) ->
                            val out = FloatArray(1)
                            Location.distanceBetween(
                                loc.latitude, loc.longitude,
                                point.latitude, point.longitude,
                                out
                            )
                            out[0] <= radiusMeters  // 公尺
                        }

                    searchResults = list
                    Log.d("NewPlaces", "✅ 找到 ${list.size} 筆（q=$query, r=$radiusMeters m）")
                } else {
                    Log.e("NewPlaces", "❌ HTTP ${resp.code()}：${resp.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("NewPlaces", "❌ 搜尋錯誤: ${e.message}")
            }
        }
    }




    Scaffold(

        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(Color.Gray.copy(alpha = 0.6f)), // 灰色且透明
                contentAlignment = Alignment.Center
            ) {
                // 中間標題
                Text(
                    text = "果然會辨識",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize=28.sp
                    ) ,
                    modifier = Modifier.padding(top = 30.dp)
                )

                // 左Icon（定位與設定）
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .padding(top = 30.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "主畫面", modifier = Modifier.size(32.dp))

                    }

                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) {innerPadding ->
        // 顯示地圖 + 使用者位置 Marker
        GoogleMap(
            modifier =
                Modifier.fillMaxSize()
                    .padding(innerPadding),//要使用innerpadding
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

}








