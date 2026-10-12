package com.example.laboratorio7.ui.screens.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.laboratorio7.data.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val name: String = "",
    val isLoggedOut: Boolean = false
)

sealed interface ProfileIntent {
    data object LogoutClicked : ProfileIntent
}

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        // Se observa el nombre guardado, por eso sigue apareciendo aunque se cierre la app
        viewModelScope.launch {
            userPreferences.userName.collect { name ->
                _uiState.update { it.copy(name = name ?: "") }
            }
        }
    }

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.LogoutClicked -> logout()
        }
    }

    private fun logout() {
        viewModelScope.launch {
            // Primero se borra el nombre y hasta después se manda a navegar al Login
            userPreferences.clearUserName()
            _uiState.update { it.copy(isLoggedOut = true) }
        }
    }
}