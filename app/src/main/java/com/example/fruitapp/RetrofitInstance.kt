package com.example.fruitapp.network

import com.example.fruitapp.NewPlacesService

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://places.googleapis.com/") // 注意這裡要有結尾的 /
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    // 新版 Places API
    val newPlacesApi: NewPlacesService = retrofit.create(NewPlacesService::class.java)
}
