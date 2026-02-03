package com.example.flightsearch.data.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val prevRequest: Flow<String>
    suspend fun saveRequest(request: String)
}