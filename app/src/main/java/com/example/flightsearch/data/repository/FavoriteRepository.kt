package com.example.flightsearch.data.repository

import com.example.flightsearch.data.database.entity.Favorite
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    suspend fun insert(favorite: Favorite)
    suspend fun delete(favorite: Favorite)
    fun getFavAirports(): Flow<List<Favorite>>
}