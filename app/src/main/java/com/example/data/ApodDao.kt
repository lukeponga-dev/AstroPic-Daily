package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ApodDao {
    @Query("SELECT * FROM apod_table ORDER BY date DESC")
    fun getAllApods(): Flow<List<ApodEntity>>

    @Query("SELECT * FROM apod_table WHERE date = :date LIMIT 1")
    suspend fun getApodByDate(date: String): ApodEntity?

    @Query("SELECT * FROM apod_table WHERE isFavorite = 1 ORDER BY date DESC")
    fun getFavorites(): Flow<List<ApodEntity>>

    @Query("UPDATE apod_table SET isFavorite = :isFavorite WHERE date = :date")
    suspend fun updateFavorite(date: String, isFavorite: Boolean)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertApod(apod: ApodEntity)

    @Query("DELETE FROM apod_table")
    suspend fun deleteAll()
}
