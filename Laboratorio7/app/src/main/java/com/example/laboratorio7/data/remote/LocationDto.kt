package com.example.laboratorio7.data.remote

import com.example.laboratorio7.data.local.LocationEntity
import kotlinx.serialization.Serializable

@Serializable
data class LocationResponseDto(
    val results: List<LocationDto>
)

@Serializable
data class LocationDto(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String
)

fun LocationDto.toEntity(): LocationEntity = LocationEntity(
    id = id,
    name = name,
    type = type,
    dimension = dimension
)
