package com.example.flightsearch.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.flightsearch.data.dao.AirportDao
import com.example.flightsearch.data.dao.FavoriteDao

@Database(entities = [Airport::class, Favorite::class], version = 1, exportSchema = false)
abstract class FlightDatabase: RoomDatabase() {
    abstract fun AirportDao(): AirportDao
    abstract fun FavoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var Instance: FlightDatabase? = null

        fun getDatabase(context: Context): FlightDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context,
                    klass = FlightDatabase::class.java,
                    name = "flight_search"
                )
                    .createFromAsset("database/flight_search.db")
                    .fallbackToDestructiveMigration().build().also { Instance = it }
            }
        }
    }
}