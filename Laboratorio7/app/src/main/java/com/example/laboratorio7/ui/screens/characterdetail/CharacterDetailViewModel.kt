package com.example.laboratorio7.ui.screens.characterdetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.laboratorio7.RickAndMortyApp
import com.example.laboratorio7.data.Character
import com.example.laboratorio7.data.local.toModel
import com.example.laboratorio7.navigation.CharacterDetailDestination
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// data es nullable porque el personaje todavía no existe mientras carga
data class CharacterDetailUiState(
    val isLoading: Boolean = true,
    val data: Character? = null,
    val hasError: Boolean = false
)

sealed interface CharacterDetailIntent {
    data object LoadingClicked : CharacterDetailIntent
    data object RetryClicked : CharacterDetailIntent
    data object FavoriteClicked : CharacterDetailIntent
}

class CharacterDetailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val characterDao = (application as RickAndMortyApp).database.characterDao()

    // Navigation guarda los argumentos de la ruta en el SavedStateHandle,
    // así que el ID se lee de ahí y la pantalla ya no tiene que recibirlo
    private val characterId = savedStateHandle.toRoute<CharacterDetailDestination>().characterId

    private val _uiState = MutableStateFlow(CharacterDetailUiState())
    val uiState: StateFlow<CharacterDetailUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        getCharacter()
    }

    fun onIntent(intent: CharacterDetailIntent) {
        when (intent) {
            CharacterDetailIntent.LoadingClicked -> showError()
            CharacterDetailIntent.RetryClicked -> getCharacter()
            CharacterDetailIntent.FavoriteClicked -> toggleFavorite()
        }
    }

    private fun getCharacter() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, hasError = false) }
            delay(2000)

            // Se queda escuchando ese personaje en Room, así el corazón cambia apenas se actualiza
            characterDao.getCharacterById(characterId).collect { entity ->
                _uiState.update { it.copy(isLoading = false, data = entity?.toModel()) }
            }
        }
    }

    private fun toggleFavorite() {
        // Si el personaje todavía no ha cargado no hay nada que actualizar
        val character = _uiState.value.data ?: return
        viewModelScope.launch {
            characterDao.updateFavorite(character.id, !character.isFavorite)
        }
    }

    private fun showError() {
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = false, hasError = true) }
    }
}