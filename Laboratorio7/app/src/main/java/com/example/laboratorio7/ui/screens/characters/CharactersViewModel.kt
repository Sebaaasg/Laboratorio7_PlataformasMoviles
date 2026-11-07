package com.example.laboratorio7.ui.screens.characters

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.laboratorio7.RickAndMortyApp
import com.example.laboratorio7.data.Character
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la pantalla, si está cargando, los datos, si hubo error y si el filtro está activo
data class CharactersUiState(
    val isLoading: Boolean = true,
    val data: List<Character> = emptyList(),
    val hasError: Boolean = false,
    val showOnlyFavorites: Boolean = false
) {
    // Lo que realmente se muestra en la lista, depende de si se están filtrando los favoritos
    val visibleCharacters: List<Character>
        get() = if (showOnlyFavorites) data.filter { it.isFavorite } else data
}

// Acciones que el usuario puede hacer en esta pantalla
// Al ser sealed, el when del ViewModel está obligado a manejarlas todas
sealed interface CharactersIntent {
    data object LoadingClicked : CharactersIntent
    data object RetryClicked : CharactersIntent
    data class FavoriteClicked(val character: Character) : CharactersIntent
    data object FavoritesFilterClicked : CharactersIntent
}

class CharactersViewModel(application: Application) : AndroidViewModel(application) {

    // Ya no se usa el DAO directo, todo pasa por el repositorio
    private val characterRepository = (application as RickAndMortyApp).characterRepository

    // El MutableStateFlow solo se modifica aquí, la UI recibe la versión de solo lectura
    private val _uiState = MutableStateFlow(CharactersUiState())
    val uiState: StateFlow<CharactersUiState> = _uiState.asStateFlow()

    // Se guarda la carga en curso para poder cancelarla
    private var loadJob: Job? = null

    init {
        getCharacters()
    }

    // Única función pública, la pantalla manda un Intent y aquí se decide qué hacer
    fun onIntent(intent: CharactersIntent) {
        when (intent) {
            CharactersIntent.LoadingClicked -> showError()
            CharactersIntent.RetryClicked -> getCharacters()
            is CharactersIntent.FavoriteClicked -> toggleFavorite(intent.character)
            CharactersIntent.FavoritesFilterClicked -> _uiState.update {
                it.copy(showOnlyFavorites = !it.showOnlyFavorites)
            }
        }
    }

    private fun getCharacters() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, hasError = false) }
            delay(4000) // simula la espera de una llamada a internet

            // Como es un Flow de Room, cada vez que se crea, edita o borra un personaje
            // llega la lista nueva y la pantalla se actualiza sola
            characterRepository.getCharacters().collect { characters ->
                _uiState.update { it.copy(isLoading = false, data = characters) }
            }
        }
    }

    // Se guarda el mismo personaje con el favorito invertido, el collect de arriba refresca la lista
    private fun toggleFavorite(character: Character) {
        viewModelScope.launch {
            characterRepository.updateCharacter(character.copy(isFavorite = !character.isFavorite))
        }
    }

    // Si tocan el layout de loading, se corta la carga y se marca el error
    private fun showError() {
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = false, hasError = true) }
    }
}
