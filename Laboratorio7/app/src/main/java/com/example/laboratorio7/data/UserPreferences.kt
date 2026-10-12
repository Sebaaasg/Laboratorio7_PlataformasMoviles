package com.example.laboratorio7.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Va fuera de la clase para que solo exista una instancia de DataStore en toda la app
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {

    private val nameKey = stringPreferencesKey("name")

    // Si devuelve null es porque no hay nombre guardado, o sea, no hay sesión
    val userName: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[nameKey]
    }

    suspend fun saveUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[nameKey] = name
        }
    }

    suspend fun clearUserName() {
        context.dataStore.edit { preferences ->
            preferences.remove(nameKey)
        }
    }
}