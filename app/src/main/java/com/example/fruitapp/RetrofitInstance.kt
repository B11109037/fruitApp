package com.example.fruitapp

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY   // 除錯期好用
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://places.googleapis.com/")  // 必須以 / 結尾
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val newPlacesApi: NewPlacesService = retrofit.create(NewPlacesService::class.java)
}
