package com.example.data

import retrofit2.http.GET
import retrofit2.http.Query

interface NasaApiService {
    @GET("planetary/apod")
    suspend fun getApod(
        @Query("api_key") apiKey: String,
        @Query("date") date: String? = null
    ): ApodResponse

    companion object {
        const val BASE_URL = "https://api.nasa.gov/"
    }
}
