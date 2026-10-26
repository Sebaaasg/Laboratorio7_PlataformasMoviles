package com.example.laboratorio7.ui.screens.locationdetails

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.laboratorio7.RickAndMortyApp
import com.example.laboratorio7.data.Location
import com.example.laboratorio7.data.local.toModel
import com.example.laboratorio7.navigation.LocationDetailDestination
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LocationDetailsUiState(
    val isLoading: Boolean = true,
    val data: Location? = null,
    val hasError: Boolean = false
)

sealed interface LocationDetailsIntent {
    data object LoadingClicked : LocationDetailsIntent
    data object RetryClicked : LocationDetailsIntent
}

class LocationDetailsViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val locationDao = (application as RickAndMortyApp).database.locationDao()

    // Igual que en el detalle de personajes, el ID sale del SavedStateHandle
    private val locationId = savedStateHandle.toRoute<LocationDetailDestination>().locationId

    private val _uiState = MutableStateFlow(LocationDetailsUiState())
    val uiState: StateFlow<LocationDetailsUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        getLocation()
    }

    fun onIntent(intent: LocationDetailsIntent) {
        when (intent) {
            LocationDetailsIntent.LoadingClicked -> showError()
            LocationDetailsIntent.RetryClicked -> getLocation()
        }
    }

    private fun getLocation() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, hasError = false) }
            delay(2000)

            locationDao.getLocationById(locationId).collect { entity ->
                _uiState.update { it.copy(isLoading = false, data = entity?.toModel()) }
            }
        }
    }

    private fun showError() {
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = false, hasError = true) }
    }
}