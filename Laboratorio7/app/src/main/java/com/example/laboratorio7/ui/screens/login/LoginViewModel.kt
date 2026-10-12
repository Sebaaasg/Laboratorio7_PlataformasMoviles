package com.example.laboratorio7.ui.screens.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.laboratorio7.data.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val name: String = "",
    val hasError: Boolean = false,
    val isLoggedIn: Boolean = false
)

sealed interface LoginIntent {
    data class NameChanged(val name: String) : LoginIntent
    data object LoginClicked : LoginIntent
}

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)

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
            // edit es suspend, así que se espera a que quede guardado antes de avisar que ya inició sesión
            userPreferences.saveUserName(name)
            _uiState.update { it.copy(isLoggedIn = true) }
        }
    }
}