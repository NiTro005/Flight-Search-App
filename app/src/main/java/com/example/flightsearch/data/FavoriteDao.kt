package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert(onConflict = IGNORE)
    suspend fun insert(departure_code: String, destination_code: String)

    @Delete
    suspend fun delete(id: Int)

    @Query("SELECT * FROM favorite")
    fun getAllFavAirport(): Flow<List<Favorite>>
}