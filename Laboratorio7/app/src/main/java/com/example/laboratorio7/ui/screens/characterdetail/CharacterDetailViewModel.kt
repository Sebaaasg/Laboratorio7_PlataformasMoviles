package com.example.laboratorio7.ui.screens.characterdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.laboratorio7.data.Character
import com.example.laboratorio7.data.CharacterDb
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

class CharacterDetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val characterDb = CharacterDb()

    // Navigation guarda los argumentos de la ruta en el SavedStateHandle,
    // así que el ID se lee de ahí y la pantalla ya no tiene que recibirlo
    private val characterId = savedStateHandle.toRoute<CharacterDetailDestination>().characterId

    private val _uiState = MutableStateFlow(CharacterDetailUiState())
    val uiState: StateFlow<CharacterDetailUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        getCharacter()
    }

    fun getCharacter() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, hasError = false) }
            delay(2000)
            _uiState.update {
                it.copy(isLoading = false, data = characterDb.getCharacterById(characterId))
            }
        }
    }

    fun onLoadingClick() {
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = false, hasError = true) }
    }
}