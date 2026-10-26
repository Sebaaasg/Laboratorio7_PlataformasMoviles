package com.example.laboratorio7.data.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {

    // Con IGNORE, si ya existe un personaje con ese id no se vuelve a insertar
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCharacters(characters: List<CharacterEntity>)

    // Devuelve un Flow, entonces cada vez que la tabla cambia llega la lista actualizada
    @Query("SELECT * FROM characters")
    fun getAllCharacters(): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE id = :id")
    fun getCharacterById(id: Int): Flow<CharacterEntity?>

    @Query("UPDATE characters SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Int, isFavorite: Boolean)

    // Se usa al cerrar sesión
    @Query("DELETE FROM characters")
    suspend fun deleteAllCharacters()
}