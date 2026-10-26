package com.example.laboratorio7.data

data class Character(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val image: String,
    // Tiene valor por defecto para que CharacterDb no tenga que cambiar
    val isFavorite: Boolean = false
)