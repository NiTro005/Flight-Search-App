package com.example.flightsearch.data

import android.content.Context
import com.example.flightsearch.data.repository.AirportRepository
import com.example.flightsearch.data.repository.FavoriteRepository
import com.example.flightsearch.data.repository.OfflineAirportRepository
import com.example.flightsearch.data.repository.OfflineFavoriteRepository

interface AppContainer {
    val airportRepository: AirportRepository
    val favoriteRepository: FavoriteRepository
}

class AppDataContainer(context: Context) : AppContainer {
    override val airportRepository: AirportRepository by lazy {
        OfflineAirportRepository(FlightDatabase.getDatabase(context).AirportDao())
    }
    override val favoriteRepository: FavoriteRepository by lazy {
        OfflineFavoriteRepository(FlightDatabase.getDatabase(context).FavoriteDao())
    }
}