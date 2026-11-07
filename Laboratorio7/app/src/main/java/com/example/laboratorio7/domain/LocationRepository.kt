package com.example.laboratorio7.domain

import com.example.laboratorio7.data.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun syncFromApi(): Boolean
    fun getLocations(): Flow<List<Location>>
    fun getLocationById(id: Int): Flow<Location?>
    suspend fun updateFavorite(id: Int, isFavorite: Boolean)
    suspend fun deleteAll()
}
