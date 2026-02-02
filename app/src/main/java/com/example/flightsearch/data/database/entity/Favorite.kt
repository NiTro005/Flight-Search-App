package com.example.flightsearch.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("favorite")
data class Favorite(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val departure_code: String,
    val destination_code: String
)