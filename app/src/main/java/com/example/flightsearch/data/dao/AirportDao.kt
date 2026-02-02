package com.example.flightsearch.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.flightsearch.data.Airport
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {
    @Query("SELECT * FROM airport ORDER BY passengers")
    fun getAllAirports(): Flow<List<Airport>>

    @Query("SELECT * FROM airport " +
            "WHERE iata_code LIKE'%:query%' OR name LIKE'%:query%' " +
            "ORDER BY passengers DESC" )
    fun getByCodeOrName(query: String): Flow<List<Airport>>
}