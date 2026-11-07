package com.example.laboratorio7.ui.screens.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.laboratorio7.RickAndMortyApp
import com.example.laboratorio7.data.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val name: String = "",
    val hasError: Boolean = false,
    val isSyncing: Boolean = false,
    // true si no se pudo descargar la información del API (por ejemplo, sin internet)
    val hasSyncError: Boolean = false,
    val isLoggedIn: Boolean = false
)

sealed interface LoginIntent {
    data class NameChanged(val name: String) : LoginIntent
    data object LoginClicked : LoginIntent
}

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)

    private val app = application as RickAndMortyApp
    private val characterRepository = app.characterRepository
    private val locationRepository = app.locationRepository

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
            _uiState.update { it.copy(isSyncing = true, hasSyncError = false) }

            // Este es el único lugar donde la app usa internet. Si no hay conexión,
            // Ktor lanza una excepción y se muestra el error en vez de cerrarse la app
            val isSynced = try {
                characterRepository.syncFromApi() && locationRepository.syncFromApi()
            } catch (e: Exception) {
                false
            }

            if (!isSynced) {
                _uiState.update { it.copy(isSyncing = false, hasSyncError = true) }
                return@launch
            }

            // El nombre se guarda hasta que los datos ya están en Room,
            // así solo hay sesión activa si la sincronización terminó
            userPreferences.saveUserName(name)

            _uiState.update { it.copy(isSyncing = false, isLoggedIn = true) }
        }
    }
}
