package com.example.fruitapp

import com.google.gson.annotations.SerializedName

data class NewPlacesResponse(
    @SerializedName("places") val places: List<Place>?
)

data class Place(
    @SerializedName("id") val id: String?,
    @SerializedName("displayName") val displayName: DisplayName?,
    @SerializedName("location") val location: LatLngLiteral?,
    @SerializedName("formattedAddress") val formattedAddress: String?,
    @SerializedName("rating") val rating: Double?,
    @SerializedName("types") val types: List<String>?
)

data class DisplayName(
    @SerializedName("text") val text: String?
)
