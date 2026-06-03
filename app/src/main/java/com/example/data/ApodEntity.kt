package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "apod_table")
data class ApodEntity(
    @PrimaryKey val date: String,
    val title: String,
    val explanation: String,
    val url: String,
    val hdurl: String?,
    val mediaType: String,
    val isFavorite: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
