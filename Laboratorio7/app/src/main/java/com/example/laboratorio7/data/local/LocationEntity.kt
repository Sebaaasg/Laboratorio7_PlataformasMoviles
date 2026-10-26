package com.example.laboratorio7.data.local

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String
)