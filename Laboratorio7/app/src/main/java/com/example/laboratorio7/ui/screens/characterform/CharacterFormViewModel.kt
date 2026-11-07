package com.example.laboratorio7.ui.screens.characterform

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.laboratorio7.RickAndMortyApp
import com.example.laboratorio7.data.Character
import com.example.laboratorio7.navigation.CharacterFormDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CharacterFormUiState(
    val isEditing: Boolean = false,
    val name: String = "",
    val status: String = "",
    val species: String = "",
    val gender: String = "",
    // Cuando pasa a true la pantalla se cierra
    val isSaved: Boolean = false
) {
    // El botón de guardar solo se habilita si todos los campos tienen algo
    val canSave: Boolean
        get() = name.isNotBlank() && status.isNotBlank() && species.isNotBlank() && gender.isNotBlank()
}

sealed interface CharacterFormIntent {
    data class NameChanged(val name: String) : CharacterFormIntent
    data class StatusChanged(val status: String) : CharacterFormIntent
    data class SpeciesChanged(val species: String) : CharacterFormIntent
    data class GenderChanged(val gender: String) : CharacterFormIntent
    data object SaveClicked : CharacterFormIntent
}

class CharacterFormViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val characterRepository = (application as RickAndMortyApp).characterRepository

    // null cuando se está creando un personaje nuevo
    private val characterId = savedStateHandle.toRoute<CharacterFormDestination>().characterId

    // Al editar se guarda el personaje original para no perder su imagen ni su favorito
    private var originalCharacter: Character? = null

    private val _uiState = MutableStateFlow(CharacterFormUiState(isEditing = characterId != null))
    val uiState: StateFlow<CharacterFormUiState> = _uiState.asStateFlow()

    init {
        if (characterId != null) {
            loadCharacter(characterId)
        }
    }

    fun onIntent(intent: CharacterFormIntent) {
        when (intent) {
            is CharacterFormIntent.NameChanged -> _uiState.update { it.copy(name = intent.name) }
            is CharacterFormIntent.StatusChanged -> _uiState.update { it.copy(status = intent.status) }
            is CharacterFormIntent.SpeciesChanged -> _uiState.update { it.copy(species = intent.species) }
            is CharacterFormIntent.GenderChanged -> _uiState.update { it.copy(gender = intent.gender) }
            CharacterFormIntent.SaveClicked -> save()
        }
    }

    // Llena el formulario con los datos actuales del personaje
    private fun loadCharacter(id: Int) {
        viewModelScope.launch {
            // first() lee una sola vez. Si se siguiera escuchando, lo que el usuario escribe se podría sobrescribir
            val character = characterRepository.getCharacterById(id).first() ?: return@launch
            originalCharacter = character
            _uiState.update {
                it.copy(
                    name = character.name,
                    status = character.status,
                    species = character.species,
                    gender = character.gender
                )
            }
        }
    }

    private fun save() {
        val state = _uiState.value
        if (!state.canSave) return

        viewModelScope.launch {
            val original = originalCharacter
            if (original == null) {
                // Personaje nuevo: sin imagen y el id lo asigna Room
                characterRepository.createCharacter(
                    Character(
                        id = 0,
                        name = state.name.trim(),
                        status = state.status.trim(),
                        species = state.species.trim(),
                        gender = state.gender.trim(),
                        image = ""
                    )
                )
            } else {
                // copy mantiene el id, la imagen y el favorito, solo cambia lo que se editó
                characterRepository.updateCharacter(
                    original.copy(
                        name = state.name.trim(),
                        status = state.status.trim(),
                        species = state.species.trim(),
                        gender = state.gender.trim()
                    )
                )
            }
            _uiState.update { it.copy(isSaved = true) }
        }
    }
}
