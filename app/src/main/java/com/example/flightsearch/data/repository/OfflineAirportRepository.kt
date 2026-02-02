package com.example.flightsearch.data.repository

import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.dao.AirportDao
import kotlinx.coroutines.flow.Flow

class OfflineAirportRepository(val airportDao: AirportDao) : AirportRepository {
    override fun getAllAirports(): Flow<List<Airport>> = airportDao.getAllAirports()

    override fun getByCodeOrName(query: String): Flow<List<Airport>> = airportDao.getByCodeOrName(query)
}