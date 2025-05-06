package com.example.fruitapp

import retrofit2.http.GET
import retrofit2.http.Query

interface TextSearchService {
    @GET("maps/api/place/textsearch/json")
    suspend fun searchPlaces(
        @Query("query") query: String,
        @Query("location") location: String,
        @Query("radius") radius: Int = 1000,
        @Query("key") apiKey: String
    ): TextSearchResponse
}
