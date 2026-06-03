package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.*
import com.example.ui.ApodScreen
import com.example.ui.ApodViewModel
import com.example.ui.theme.NasaDailyTheme
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Data Components
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val cacheSize = (10 * 1024 * 1024).toLong() // 10 MB
        val cache = okhttp3.Cache(cacheDir, cacheSize)

        val client = OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val apiService = Retrofit.Builder()
            .baseUrl(NasaApiService.BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(NasaApiService::class.java)

        val database = AppDatabase.getDatabase(this)
        val repository = ApodRepository(apiService, database.apodDao())

        val viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ApodViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return ApodViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        enableEdgeToEdge()
        setContent {
            NasaDailyTheme {
                val viewModel: ApodViewModel = viewModel(factory = viewModelFactory)
                ApodScreen(viewModel = viewModel)
            }
        }
    }
}
