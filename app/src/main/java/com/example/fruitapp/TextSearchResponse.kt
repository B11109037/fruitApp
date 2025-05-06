package com.example.fruitapp

data class TextSearchResponse(
    val results: List<TextSearchResult>
)

data class TextSearchResult(
    val name: String,
    val geometry: Geometry
)

data class Geometry(
    val location: LatLngResult
)

data class LatLngResult(
    val lat: Double,
    val lng: Double
)
