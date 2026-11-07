package com.example.laboratorio7.data.remote

import com.example.laboratorio7.data.local.CharacterEntity
import kotlinx.serialization.Serializable

// El API no devuelve la lista directo, la manda dentro de "results"
@Serializable
data class CharacterResponseDto(
    val results: List<CharacterDto>
)

// Solo se declaran los campos que usa la app, el resto se ignora
@Serializable
data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val image: String
)

// DTO -> Entity, se usa al guardar en Room lo que llegó del API
fun CharacterDto.toEntity(): CharacterEntity = CharacterEntity(
    id = id,
    name = name,
    status = status,
    species = species,
    gender = gender,
    image = image
)
