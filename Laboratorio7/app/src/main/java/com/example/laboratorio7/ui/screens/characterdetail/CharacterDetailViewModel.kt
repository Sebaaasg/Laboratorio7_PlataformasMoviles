package com.example.laboratorio7.ui.screens.characterdetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.laboratorio7.RickAndMortyApp
import com.example.laboratorio7.data.Character
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
    val hasError: Boolean = false,
    val showDeleteDialog: Boolean = false,
    // Cuando pasa a true la pantalla regresa a la lista
    val isDeleted: Boolean = false
)

sealed interface CharacterDetailIntent {
    data object LoadingClicked : CharacterDetailIntent
    data object RetryClicked : CharacterDetailIntent
    data object FavoriteClicked : CharacterDetailIntent
    data object DeleteClicked : CharacterDetailIntent
    data object DeleteConfirmed : CharacterDetailIntent
    data object DeleteDismissed : CharacterDetailIntent
}

class CharacterDetailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val characterRepository = (application as RickAndMortyApp).characterRepository

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
            CharacterDetailIntent.DeleteClicked -> _uiState.update { it.copy(showDeleteDialog = true) }
            CharacterDetailIntent.DeleteDismissed -> _uiState.update { it.copy(showDeleteDialog = false) }
            CharacterDetailIntent.DeleteConfirmed -> deleteCharacter()
        }
    }

    private fun getCharacter() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, hasError = false) }
            delay(2000)

            // Se queda escuchando ese personaje en Room. Si se edita o cambia el favorito,
            // el detalle se actualiza solo sin volver a pedir nada
            characterRepository.getCharacterById(characterId).collect { character ->
                _uiState.update { it.copy(isLoading = false, data = character) }
            }
        }
    }

    private fun toggleFavorite() {
        // Si el personaje todavía no ha cargado no hay nada que actualizar
        val character = _uiState.value.data ?: return
        viewModelScope.launch {
            characterRepository.updateCharacter(character.copy(isFavorite = !character.isFavorite))
        }
    }

    private fun deleteCharacter() {
        val character = _uiState.value.data ?: return
        viewModelScope.launch {
            characterRepository.deleteCharacter(character)
            _uiState.update { it.copy(showDeleteDialog = false, isDeleted = true) }
        }
    }

    private fun showError() {
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = false, hasError = true) }
    }
}
