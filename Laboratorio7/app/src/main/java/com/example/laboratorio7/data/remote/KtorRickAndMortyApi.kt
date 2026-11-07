package com.example.laboratorio7.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode

private const val BASE_URL = "https://rickandmortyapi.com/api"

class KtorRickAndMortyApi(
    private val httpClient: HttpClient
) : RickAndMortyApi {

    // Sin parámetros el API devuelve la primera página, 20 personajes
    override suspend fun getCharacters(): List<CharacterDto> {
        val response = httpClient.get("$BASE_URL/character")
        if (response.status == HttpStatusCode.OK) {
            return response.body<CharacterResponseDto>().results
        } else {
            return emptyList()
        }
    }

    override suspend fun getLocations(): List<LocationDto> {
        val response = httpClient.get("$BASE_URL/location")
        if (response.status == HttpStatusCode.OK) {
            return response.body<LocationResponseDto>().results
        } else {
            return emptyList()
        }
    }
}
