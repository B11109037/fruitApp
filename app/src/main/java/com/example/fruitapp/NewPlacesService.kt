package com.example.fruitapp

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface NewPlacesService {
    @POST("v1/places:searchText")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun searchPlaces(
        @Body request: NewPlacesRequest,
        @Header("X-Goog-Api-Key") apiKey: String,
        @Header("X-Goog-FieldMask")
        fieldMask: String ="places.id,places.displayName,places.location,places.formattedAddress,places.rating,places.types"

    ): retrofit2.Response<NewPlacesResponse>
}
//interface NewPlacesService {
//    @POST("v1/places:searchText")
//    @Headers("Content-Type: application/json")
//    suspend fun searchPlaces(
//        @Body request: NewPlacesRequest,
//        @Header("X-Goog-Api-Key") apiKey: String,
//        @Header("X-Goog-FieldMask") fieldMask: String
//    ): NewPlacesResponse
//}
