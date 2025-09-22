package com.example.fruitapp

import com.google.gson.annotations.SerializedName

data class NewPlacesRequest(
    @SerializedName("textQuery") val textQuery: String,
    @SerializedName("locationBias") val locationBias: LocationBias? = null
)

data class LocationBias(
    @SerializedName("circle") val circle: Circle
)

data class Circle(
    @SerializedName("center") val center: LatLngLiteral,
    @SerializedName("radius") val radius: Double
)

data class LatLngLiteral(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
)
