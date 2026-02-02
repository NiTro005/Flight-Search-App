package com.example.flightsearch.data.repository

import com.example.flightsearch.data.Favorite
import com.example.flightsearch.data.dao.FavoriteDao
import kotlinx.coroutines.flow.Flow

class OfflineFavoriteRepository(val favoriteDao: FavoriteDao): FavoriteRepository {
    override suspend fun delete(favorite: Favorite) = favoriteDao.delete(favorite)

    override suspend fun insert(favorite: Favorite) = favoriteDao.insert(favorite)

    override fun getFavAirports(): Flow<List<Favorite>> = favoriteDao.getAllFavAirport()
}