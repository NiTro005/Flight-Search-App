package com.example.flightsearch.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("airport")
data class Airport(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val iata_code: String,
    val name: String,
    val passengers: Int
)