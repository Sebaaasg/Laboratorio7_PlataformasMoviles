package com.example.laboratorio7.data.local

import androidx.room3.Entity
import androidx.room3.PrimaryKey

// Cada objeto es una fila de la tabla characters
@Entity(tableName = "characters")
data class CharacterEntity(
    // No es autogenerado porque el id ya viene de CharacterDb
    @PrimaryKey
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val image: String,
    val isFavorite: Boolean = false
)