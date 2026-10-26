package com.example.laboratorio7

import android.app.Application
import androidx.room3.Room
import com.example.laboratorio7.data.local.AppDatabase

class RickAndMortyApp : Application() {

    // Solo existe una Application, entonces la base de datos también es una sola para toda la app.
    // by lazy la crea la primera vez que alguien la pide
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "rick_and_morty.db"
        ).build()
    }
}