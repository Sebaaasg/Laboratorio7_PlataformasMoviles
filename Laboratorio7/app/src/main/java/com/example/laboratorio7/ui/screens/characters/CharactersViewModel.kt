package com.example.laboratorio7.ui.screens.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laboratorio7.data.Character
import com.example.laboratorio7.data.CharacterDb
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la pantalla: si está cargando, los datos y si hubo error
data class CharactersUiState(
    val isLoading: Boolean = true,
    val data: List<Character> = emptyList(),
    val hasError: Boolean = false
)

class CharactersViewModel : ViewModel() {

    private val characterDb = CharacterDb()

    // El MutableStateFlow solo se modifica aquí; la UI recibe la versión de solo lectura
    private val _uiState = MutableStateFlow(CharactersUiState())
    val uiState: StateFlow<CharactersUiState> = _uiState.asStateFlow()

    // Guardamos la carga en curso para poder cancelarla
    private var loadJob: Job? = null

    init {
        getCharacters()
    }

    fun getCharacters() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, hasError = false) }
            delay(4000) // simula la espera de una llamada a internet
            _uiState.update {
                it.copy(isLoading = false, data = characterDb.getAllCharacters())
            }
        }
    }

    // Si tocan el layout de loading, se corta la carga y se marca el error
    fun onLoadingClick() {
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = false, hasError = true) }
    }
}