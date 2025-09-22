package com.example.fruitapp

import com.google.gson.annotations.SerializedName

data class NewPlacesResponse(
    @SerializedName("places") val places: List<Place>
)

data class Place(
    @SerializedName("id") val id: String?,
    @SerializedName("displayName") val displayName: DisplayName?,
    @SerializedName("location") val location: LatLngLiteral?
)

data class DisplayName(
    @SerializedName("text") val text: String
)
