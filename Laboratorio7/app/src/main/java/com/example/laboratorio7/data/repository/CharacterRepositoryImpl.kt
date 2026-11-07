package com.example.laboratorio7.data.repository

import com.example.laboratorio7.data.Character
import com.example.laboratorio7.data.local.CharacterDao
import com.example.laboratorio7.data.local.toEntity
import com.example.laboratorio7.data.local.toModel
import com.example.laboratorio7.data.remote.RickAndMortyApi
import com.example.laboratorio7.data.remote.toEntity
import com.example.laboratorio7.domain.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CharacterRepositoryImpl(
    private val characterDao: CharacterDao,
    private val api: RickAndMortyApi
) : CharacterRepository {

    // Solo lo llama el Login. Después de eso la app trabaja únicamente con Room
    override suspend fun syncFromApi(): Boolean {
        val remoteCharacters = api.getCharacters()
        characterDao.insertCharacters(remoteCharacters.map { dto -> dto.toEntity() })
        return remoteCharacters.isNotEmpty()
    }

    override fun getCharacters(): Flow<List<Character>> {
        return characterDao.getAllCharacters().map { list ->
            list.map { entity -> entity.toModel() }
        }
    }

    override fun getCharacterById(id: Int): Flow<Character?> {
        return characterDao.getCharacterById(id).map { entity -> entity?.toModel() }
    }

    // Como el API es de solo lectura, crear, editar y borrar solo pasa en Room
    override suspend fun createCharacter(character: Character) {
        // Con id = 0 Room genera uno nuevo
        characterDao.insertCharacter(character.copy(id = 0).toEntity())
    }

    override suspend fun updateCharacter(character: Character) {
        characterDao.updateCharacter(character.toEntity())
    }

    override suspend fun deleteCharacter(character: Character) {
        characterDao.deleteCharacter(character.toEntity())
    }

    override suspend fun deleteAll() {
        characterDao.deleteAllCharacters()
    }
}
