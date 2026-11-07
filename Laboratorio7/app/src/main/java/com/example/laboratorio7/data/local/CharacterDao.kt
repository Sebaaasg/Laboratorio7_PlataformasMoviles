package com.example.laboratorio7.data.local

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {

    // Con IGNORE, si ya existe un personaje con ese id no se vuelve a insertar
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCharacters(characters: List<CharacterEntity>)

    @Insert
    suspend fun insertCharacter(character: CharacterEntity)

    // Busca la fila por id y reemplaza todos sus campos
    @Update
    suspend fun updateCharacter(character: CharacterEntity)

    @Delete
    suspend fun deleteCharacter(character: CharacterEntity)

    // Devuelve un Flow, entonces cada vez que la tabla cambia llega la lista actualizada
    @Query("SELECT * FROM characters ORDER BY id")
    fun getAllCharacters(): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE id = :id")
    fun getCharacterById(id: Int): Flow<CharacterEntity?>

    // Se usa al cerrar sesión
    @Query("DELETE FROM characters")
    suspend fun deleteAllCharacters()
}
