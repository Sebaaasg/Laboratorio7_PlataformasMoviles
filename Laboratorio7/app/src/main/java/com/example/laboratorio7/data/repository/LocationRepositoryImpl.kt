package com.example.laboratorio7.data.repository

import com.example.laboratorio7.data.Location
import com.example.laboratorio7.data.local.LocationDao
import com.example.laboratorio7.data.local.toModel
import com.example.laboratorio7.data.remote.RickAndMortyApi
import com.example.laboratorio7.data.remote.toEntity
import com.example.laboratorio7.domain.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocationRepositoryImpl(
    private val locationDao: LocationDao,
    private val api: RickAndMortyApi
) : LocationRepository {

    override suspend fun syncFromApi(): Boolean {
        val remoteLocations = api.getLocations()
        locationDao.insertLocations(remoteLocations.map { dto -> dto.toEntity() })
        return remoteLocations.isNotEmpty()
    }

    override fun getLocations(): Flow<List<Location>> {
        return locationDao.getAllLocations().map { list ->
            list.map { entity -> entity.toModel() }
        }
    }

    override fun getLocationById(id: Int): Flow<Location?> {
        return locationDao.getLocationById(id).map { entity -> entity?.toModel() }
    }

    override suspend fun updateFavorite(id: Int, isFavorite: Boolean) {
        locationDao.updateFavorite(id, isFavorite)
    }

    override suspend fun deleteAll() {
        locationDao.deleteAllLocations()
    }
}
