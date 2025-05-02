package com.example.fruitapp
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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



@OptIn(ExperimentalPermissionsApi::class)
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
    //記住附近的地點
    val nearbyPlaces = remember { mutableStateListOf<Pair<String, LatLng>>() }


    // 請求定位權限
    LaunchedEffect(Unit) {
        locationPermission.launchPermissionRequest()
    }

    // 拿目前定位
    LaunchedEffect(locationPermission.status) {
        if (locationPermission.status.isGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    currentLocation = LatLng(it.latitude, it.longitude)
                }
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

    // 顯示地圖 + 使用者位置 Marker
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        currentLocation?.let {
            Marker(
                state = MarkerState(position = it),
                title = "你在這裡"
            )
        }

    }
}







//
//@OptIn(ExperimentalPermissionsApi::class)
//@Composable
//fun MapScreen() {
//    val context = LocalContext.current
//    //找尋權限
//    val locationPermission = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
//    //測試座標
//    val taipei101 = LatLng(25.01337780019572, 121.54053362549432)
//
//    // ➕ 新增 flag，避免多次初始化
//    var placesInitialized by remember { mutableStateOf(false) }
//
//    //記住MARK水果行
//    val nearbyPlaces = remember { mutableStateListOf<Place>() }
//
//
//    //找尋權限要求
//    LaunchedEffect(Unit) {
//        locationPermission.launchPermissionRequest()
//    }
//
//    // ✅ 初始化 Places 一次（只做一次）
//    LaunchedEffect(Unit) {
//        if (!Places.isInitialized()) {
//            try {
//                Places.initialize(context, "AIzaSyAuEoMZPDV9xWY1F7-ghm_xYG9X-uvhpWc")
//                placesInitialized = true
//                Log.d("MapScreen", "Places 初始化完成")
//            } catch (e: Exception) {
//                Log.e("MapScreen", "Places 初始化失敗：${e.message}")
//            }
//        } else {
//            placesInitialized = true
//        }
//    }
//
//    // ✅ 只有在已初始化且有權限時才搜尋地點
//    LaunchedEffect(placesInitialized, locationPermission.status) {
//        if (placesInitialized && locationPermission.status.isGranted) {
//            try {
//                val placesClient = Places.createClient(context)
//                val placeFields = listOf(
//                    Place.Field.NAME,
//                    Place.Field.LAT_LNG,
//                    Place.Field.TYPES
//                )
//                val request = FindCurrentPlaceRequest.newInstance(placeFields)
//                //去google找水果行的位置
//                placesClient.findCurrentPlace(request)
//                    .addOnSuccessListener { response ->
//                        for (placeLikelihood in response.placeLikelihoods) {
//                            val place = placeLikelihood.place
//                            if (place.types?.contains(Place.Type.GROCERY_OR_SUPERMARKET) == true) {
//                                Log.d("NearbyPlace", "找到水果行: ${place.name} at ${place.latLng}")
//                                nearbyPlaces.add(place)//存入
//                            }
////                            Log.d("NearbyPlace", "名稱：${place.name}, 種類：${place.types}, 座標：${place.latLng}")
////                            place.latLng?.let { nearbyPlaces.add(place) }
//                        }
//                    }
//                    .addOnFailureListener { exception ->
//                        Log.e("NearbyPlace", "找地點失敗: $exception")
//                    }
//            } catch (e: Exception) {
//                Log.e("MapScreen", "Places 使用錯誤: ${e.message}")
//            }
//        }
//    }
//    //記住照相機現在的位置
//    val cameraPositionState = rememberCameraPositionState {
//        position = CameraPosition.fromLatLngZoom(taipei101, 15f)
//    }
//   //劃出GOOGLE MAP
//    GoogleMap(
//        modifier = Modifier.fillMaxSize(),
//        cameraPositionState = cameraPositionState
//    ) {
//        // ➕ 額外畫出附近水果行的 marker
//        nearbyPlaces.forEach { place ->
//            place.latLng?.let { latLng ->
//                Marker(
//                    state = MarkerState(position = latLng),
//                    title = place.name ?: "水果行"
//                )
//            }
//        }
//    }
//}