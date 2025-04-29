package com.example.fruitapp.api

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

data class UploadResponse(val result: String)

interface ApiService {
    @Multipart
    @POST("upload")
    fun uploadImage(
        @Part image: MultipartBody.Part
    ): Call<UploadResponse>
}