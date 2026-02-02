package com.example.flightsearch.data.repository

import kotlinx.coroutines.flow.Flow

interface serPreferencesRepository {
    val prevRequest: Flow<String>
    suspend fun saveRequest(request: String)
}