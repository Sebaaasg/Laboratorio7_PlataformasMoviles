package com.example.laboratorio7.domain

import com.example.laboratorio7.data.Character
import kotlinx.coroutines.flow.Flow

// Los ViewModels solo hablan con esta interfaz, no saben si los datos vienen de Room o del API
interface CharacterRepository {
    // Devuelve true si llegaron personajes del API
    suspend fun syncFromApi(): Boolean
    fun getCharacters(): Flow<List<Character>>
    fun getCharacterById(id: Int): Flow<Character?>
    suspend fun createCharacter(character: Character)
    suspend fun updateCharacter(character: Character)
    suspend fun deleteCharacter(character: Character)
    suspend fun deleteAll()
}
