package com.example.data

import com.example.BuildConfig
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class ApodRepository(
    private val apiService: NasaApiService,
    private val apodDao: ApodDao
) {
    val allApods: Flow<List<ApodEntity>> = apodDao.getAllApods()
    val favorites: Flow<List<ApodEntity>> = apodDao.getFavorites()

    suspend fun refreshApod(date: String? = null) {
        val response = apiService.getApod(
            apiKey = BuildConfig.NASA_API_KEY,
            date = date
        )
        val entity = ApodEntity(
            date = response.date,
            title = response.title,
            explanation = response.explanation,
            url = response.url,
            hdurl = response.hdurl,
            mediaType = response.mediaType,
            copyright = response.copyright
        )
        apodDao.insertApod(entity)
        if (apodDao.getApodByDate(entity.date) == null) {
            apodDao.insertApod(entity)
        } else {
            // Update fields except isFavorite if it already exists, or just ignore since we use IGNORE
        }
    }

    suspend fun getApodByDate(date: String): ApodEntity? {
        return apodDao.getApodByDate(date)
    }

    suspend fun toggleFavorite(date: String, isFavorite: Boolean) {
        apodDao.updateFavorite(date, isFavorite)
    }

    fun getTodayString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}
