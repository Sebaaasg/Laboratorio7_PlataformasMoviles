package com.example.laboratorio7.ui.screens.locations

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.laboratorio7.RickAndMortyApp
import com.example.laboratorio7.data.Location
import com.example.laboratorio7.data.local.toModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LocationsUiState(
    val isLoading: Boolean = true,
    val data: List<Location> = emptyList(),
    val hasError: Boolean = false
)

sealed interface LocationsIntent {
    data object LoadingClicked : LocationsIntent
    data object RetryClicked : LocationsIntent
}

// Misma idea que CharactersViewModel, pero con ubicaciones
class LocationsViewModel(application: Application) : AndroidViewModel(application) {

    private val locationDao = (application as RickAndMortyApp).database.locationDao()

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
        }
    }

    private fun getLocations() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, hasError = false) }
            delay(4000)

            // Ahora se lee de Room y no de LocationDb
            locationDao.getAllLocations().collect { entities ->
                _uiState.update { state ->
                    state.copy(isLoading = false, data = entities.map { it.toModel() })
                }
            }
        }
    }

    private fun showError() {
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = false, hasError = true) }
    }
}