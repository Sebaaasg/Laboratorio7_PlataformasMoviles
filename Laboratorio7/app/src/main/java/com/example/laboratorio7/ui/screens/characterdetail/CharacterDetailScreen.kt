package com.example.laboratorio7.ui.screens.characterdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.laboratorio7.data.Character
import com.example.laboratorio7.ui.components.CharacterImage
import com.example.laboratorio7.ui.components.ErrorLayout
import com.example.laboratorio7.ui.components.FavoriteButton
import com.example.laboratorio7.ui.components.LoadingLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    onBackClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    viewModel: CharacterDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Se guarda en una variable local para que Kotlin sepa que ya no es null
    val character = uiState.data

    // Después de borrar el personaje ya no hay nada que mostrar, se regresa a la lista
    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) {
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Character Detail") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                actions = {
                    // Las acciones solo aparecen cuando ya se cargó el personaje
                    if (character != null) {
                        FavoriteButton(
                            isFavorite = character.isFavorite,
                            onClick = { viewModel.onIntent(CharacterDetailIntent.FavoriteClicked) }
                        )
                        IconButton(onClick = { onEditClick(character.id) }) {
                            Icon(imageVector = Icons.Filled.Edit, contentDescription = "Editar")
                        }
                        IconButton(onClick = { viewModel.onIntent(CharacterDetailIntent.DeleteClicked) }) {
                            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Eliminar")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)

        when {
            uiState.isLoading -> LoadingLayout(
                onClick = { viewModel.onIntent(CharacterDetailIntent.LoadingClicked)},
                modifier = contentModifier
            )

            uiState.hasError -> ErrorLayout(
                message = "Error al obtener el personaje.",
                onRetryClick = { viewModel.onIntent(CharacterDetailIntent.RetryClicked) },
                modifier = contentModifier
            )

            character != null -> CharacterDetailContent(
                character = character,
                modifier = contentModifier
            )
        }
    }

    // Se pide confirmación antes de borrar porque no se puede deshacer
    if (uiState.showDeleteDialog && character != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onIntent(CharacterDetailIntent.DeleteDismissed) },
            title = { Text("Eliminar personaje") },
            text = { Text("¿Seguro que quieres eliminar a ${character.name}?") },
            confirmButton = {
                TextButton(onClick = { viewModel.onIntent(CharacterDetailIntent.DeleteConfirmed) }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onIntent(CharacterDetailIntent.DeleteDismissed) }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun CharacterDetailContent(character: Character, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CharacterImage(image = character.image, name = character.name, size = 180.dp)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = character.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Se reutiliza un Composable pequeño para las filas de datos
        DetailRow(label = "Species:", value = character.species)
        Spacer(modifier = Modifier.height(12.dp))
        DetailRow(label = "Status:", value = character.status)
        Spacer(modifier = Modifier.height(12.dp))
        DetailRow(label = "Gender:", value = character.gender)
    }
}

// Composable de ayuda para alinear los textos del detalle
@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween // Esto empuja los textos a los extremos
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontWeight = FontWeight.Medium)
    }
}
