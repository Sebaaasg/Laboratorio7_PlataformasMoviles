package com.example.laboratorio7.data.local

import androidx.room3.Entity
import androidx.room3.PrimaryKey

// Cada objeto es una fila de la tabla characters
@Entity(tableName = "characters")
data class CharacterEntity(
    // Los del API ya traen su id. Para los que crea el usuario se manda 0
    // y Room les asigna el siguiente disponible
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val image: String,
    val isFavorite: Boolean = false
)
