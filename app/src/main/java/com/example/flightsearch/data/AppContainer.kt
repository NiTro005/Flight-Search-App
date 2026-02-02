package com.example.flightsearch.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.flightsearch.data.repository.AirportRepository
import com.example.flightsearch.data.repository.DataStoreRepository
import com.example.flightsearch.data.repository.FavoriteRepository
import com.example.flightsearch.data.repository.OfflineAirportRepository
import com.example.flightsearch.data.repository.OfflineFavoriteRepository
import com.example.flightsearch.data.repository.UserPreferencesRepository

private const val REQUEST_USER_PREFERENCES = "request_preferences"
private val Context.datastore: DataStore<Preferences> by preferencesDataStore(
    name = REQUEST_USER_PREFERENCES
)


interface AppContainer {
    val airportRepository: AirportRepository
    val favoriteRepository: FavoriteRepository
    val dataStoreRepository: UserPreferencesRepository
}

class AppDataContainer(context: Context) : AppContainer {
    override val airportRepository: AirportRepository by lazy {
        OfflineAirportRepository(FlightDatabase.getDatabase(context).AirportDao())
    }
    override val favoriteRepository: FavoriteRepository by lazy {
        OfflineFavoriteRepository(FlightDatabase.getDatabase(context).FavoriteDao())
    }
    override val dataStoreRepository: UserPreferencesRepository by lazy {
        DataStoreRepository(dataStore = context.datastore)
    }
}