package com.example.laboratorio7

import android.app.Application
import androidx.room3.Room
import com.example.laboratorio7.data.local.AppDatabase
import com.example.laboratorio7.data.remote.HttpClientFactory
import com.example.laboratorio7.data.remote.KtorRickAndMortyApi
import com.example.laboratorio7.data.remote.RickAndMortyApi
import com.example.laboratorio7.data.repository.CharacterRepositoryImpl
import com.example.laboratorio7.data.repository.LocationRepositoryImpl
import com.example.laboratorio7.domain.CharacterRepository
import com.example.laboratorio7.domain.LocationRepository
import io.ktor.client.HttpClient

class RickAndMortyApp : Application() {

    // Solo existe una Application, entonces la base de datos también es una sola para toda la app.
    // by lazy la crea la primera vez que alguien la pide
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "rick_and_morty.db"
        )
            // Si cambia la versión se borra la base vieja. No se pierde nada importante
            // porque todo se vuelve a descargar del API en el login
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    // Igual que la base de datos: un solo HttpClient para toda la app
    val httpClient: HttpClient by lazy {
        HttpClientFactory.create()
    }

    val api: RickAndMortyApi by lazy {
        KtorRickAndMortyApi(httpClient)
    }

    val characterRepository: CharacterRepository by lazy {
        CharacterRepositoryImpl(
            characterDao = database.characterDao(),
            api = api
        )
    }

    val locationRepository: LocationRepository by lazy {
        LocationRepositoryImpl(
            locationDao = database.locationDao(),
            api = api
        )
    }
}
