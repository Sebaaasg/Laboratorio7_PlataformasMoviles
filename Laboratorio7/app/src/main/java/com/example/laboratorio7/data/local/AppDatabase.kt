package com.example.laboratorio7.data.local

import androidx.room3.Database
import androidx.room3.RoomDatabase

// Base de datos con las dos tablas. Room genera la implementación con KSP
@Database(
    entities = [
        CharacterEntity::class,
        LocationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun locationDao(): LocationDao
}