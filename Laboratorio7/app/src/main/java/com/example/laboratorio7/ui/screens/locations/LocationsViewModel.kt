package com.example.laboratorio7.ui.screens.locations

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.laboratorio7.RickAndMortyApp
import com.example.laboratorio7.data.Location
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Las tres opciones del filtro, con el texto que se muestra en cada chip
enum class LocationFilter(val label: String) {
    ALL("Todas"),
    FAVORITES("Favoritas"),
    NOT_FAVORITES("No favoritas")
}

data class LocationsUiState(
    val isLoading: Boolean = true,
    val data: List<Location> = emptyList(),
    val hasError: Boolean = false,
    val filter: LocationFilter = LocationFilter.ALL
) {
    // Igual que en personajes: la lista completa se guarda en data y aquí se decide qué se ve
    val visibleLocations: List<Location>
        get() = when (filter) {
            LocationFilter.ALL -> data
            LocationFilter.FAVORITES -> data.filter { it.isFavorite }
            LocationFilter.NOT_FAVORITES -> data.filter { !it.isFavorite }
        }
}

sealed interface LocationsIntent {
    data object LoadingClicked : LocationsIntent
    data object RetryClicked : LocationsIntent
    data class FavoriteClicked(val location: Location) : LocationsIntent
    data class FilterSelected(val filter: LocationFilter) : LocationsIntent
}

class LocationsViewModel(application: Application) : AndroidViewModel(application) {

    private val locationRepository = (application as RickAndMortyApp).locationRepository

    private val _uiState = MutableStateFlow(LocationsUiState())
    val uiState: StateFlow<LocationsUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        getLocations()
    }

    fun onIntent(intent: LocationsIntent) {
        when (intent) {
            LocationsIntent.LoadingClicked -> showError()
            LocationsIntent.RetryClicked -> getLocations()
            is LocationsIntent.FavoriteClicked -> toggleFavorite(intent.location)
            is LocationsIntent.FilterSelected -> _uiState.update { it.copy(filter = intent.filter) }
        }
    }

    private fun getLocations() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, hasError = false) }
            delay(4000)

            locationRepository.getLocations().collect { locations ->
                _uiState.update { it.copy(isLoading = false, data = locations) }
            }
        }
    }

    private fun toggleFavorite(location: Location) {
        viewModelScope.launch {
            locationRepository.updateFavorite(location.id, !location.isFavorite)
        }
    }

    private fun showError() {
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = false, hasError = true) }
    }
}
