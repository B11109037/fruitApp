package com.example.fruitapp

import com.google.gson.annotations.SerializedName

data class NewPlacesRequest(
    @SerializedName("textQuery") val textQuery: String,
    // 影響排序（選用）
    @SerializedName("locationBias") val locationBias: LocationBias? = null,
    // 🔒硬限制範圍（半徑真的會生效）
    @SerializedName("locationRestriction") val locationRestriction: LocationRestriction? = null,
    // 可選：限制回傳筆數
    @SerializedName("maxResultCount") val maxResultCount: Int? = 20
)

data class LocationBias(
    @SerializedName("circle") val circle: Circle
)

data class LocationRestriction(
    @SerializedName("circle") val circle: Circle? = null,
    @SerializedName("rectangle") val rectangle: Rectangle? = null
)

data class Rectangle(
    @SerializedName("low")  val low: LatLngLiteral,
    @SerializedName("high") val high: LatLngLiteral
)

data class Circle(
    @SerializedName("center") val center: LatLngLiteral,
    // 公尺，必須 Double
    @SerializedName("radius") val radius: Double
)

data class LatLngLiteral(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
)
