package com.example.laboratorio7.ui.screens.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.laboratorio7.RickAndMortyApp
import com.example.laboratorio7.data.CharacterDb
import com.example.laboratorio7.data.LocationDb
import com.example.laboratorio7.data.UserPreferences
import com.example.laboratorio7.data.local.toEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val name: String = "",
    val hasError: Boolean = false,
    val isSyncing: Boolean = false,
    val isLoggedIn: Boolean = false
)

sealed interface LoginIntent {
    data class NameChanged(val name: String) : LoginIntent
    data object LoginClicked : LoginIntent
}

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)

    private val database = (application as RickAndMortyApp).database
    private val characterDao = database.characterDao()
    private val locationDao = database.locationDao()

    // Estos dos ya solo se usan aquí, para llenar Room en la sincronización inicial
    private val characterDb = CharacterDb()
    private val locationDb = LocationDb()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onIntent(intent: LoginIntent) {
        when (intent) {
            // Si vuelve a escribir, se quita el error
            is LoginIntent.NameChanged -> _uiState.update { it.copy(name = intent.name, hasError = false) }
            LoginIntent.LoginClicked -> login()
        }
    }

    private fun login() {
        val name = _uiState.value.name.trim()

        // No se guarda un nombre vacío, porque que exista el nombre es lo que indica que hay sesión
        if (name.isEmpty()) {
            _uiState.update { it.copy(hasError = true) }
            return
        }

        viewModelScope.launch {
            // Mientras isSyncing sea true el botón está deshabilitado y se muestra el progress
            _uiState.update { it.copy(isSyncing = true) }

            syncData()

            // El nombre se guarda hasta que los datos ya están en Room,
            // así solo hay sesión activa si la sincronización terminó
            userPreferences.saveUserName(name)

            _uiState.update { it.copy(isSyncing = false, isLoggedIn = true) }
        }
    }

    // Pasa los datos de CharacterDb y LocationDb a sus tablas en Room
    private suspend fun syncData() {
        // first() toma lo que hay en la tabla en ese momento, una sola vez
        val savedCharacters = characterDao.getAllCharacters().first()
        val savedLocations = locationDao.getAllLocations().first()

        // Si las tablas ya tienen datos, la sincronización ya se hizo antes y no se repite
        if (savedCharacters.isNotEmpty() && savedLocations.isNotEmpty()) return

        delay(4000) // simula una carga real

        characterDao.insertCharacters(characterDb.getAllCharacters().map { it.toEntity() })
        locationDao.insertLocations(locationDb.getAllLocations().map { it.toEntity() })
    }
}