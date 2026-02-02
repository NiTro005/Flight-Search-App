package com.example.flightsearch.data.repository

import com.example.flightsearch.data.Airport
import kotlinx.coroutines.flow.Flow

interface AirportRepository {
    fun getAllAirports(): Flow<List<Airport>>
    fun getByCodeOrName(query: String): Flow<List<Airport>>
}