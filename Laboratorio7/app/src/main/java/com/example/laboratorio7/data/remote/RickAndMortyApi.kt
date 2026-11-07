package com.example.laboratorio7.data.remote

// El repositorio solo conoce esta interfaz, no sabe que por debajo se usa Ktor
interface RickAndMortyApi {
    suspend fun getCharacters(): List<CharacterDto>
    suspend fun getLocations(): List<LocationDto>
}
