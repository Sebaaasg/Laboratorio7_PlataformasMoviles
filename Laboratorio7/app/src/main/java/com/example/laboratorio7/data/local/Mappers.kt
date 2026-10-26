package com.example.laboratorio7.data.local

import com.example.laboratorio7.data.Character
import com.example.laboratorio7.data.Location

// Entity -> Model, lo usan los ViewModels al leer de Room, así la UI nunca ve un Entity
fun CharacterEntity.toModel(): Character = Character(
    id = id,
    name = name,
    status = status,
    species = species,
    gender = gender,
    image = image,
    isFavorite = isFavorite
)

// Model -> Entity, lo usa la sincronización inicial para guardar lo de CharacterDb
fun Character.toEntity(): CharacterEntity = CharacterEntity(
    id = id,
    name = name,
    status = status,
    species = species,
    gender = gender,
    image = image,
    isFavorite = isFavorite
)

fun LocationEntity.toModel(): Location = Location(
    id = id,
    name = name,
    type = type,
    dimension = dimension
)

fun Location.toEntity(): LocationEntity = LocationEntity(
    id = id,
    name = name,
    type = type,
    dimension = dimension
)