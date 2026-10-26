package com.example.laboratorio7.ui.screens.characters

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.laboratorio7.data.Character
import com.example.laboratorio7.ui.components.EmptyLayout
import com.example.laboratorio7.ui.components.ErrorLayout
import com.example.laboratorio7.ui.components.FavoriteButton
import com.example.laboratorio7.ui.components.LoadingLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharactersScreen(
    onCharacterClick: (Int) -> Unit,
    viewModel: CharactersViewModel = viewModel()
) {
    // La pantalla ya no busca los datos, solo observa el estado que expone el ViewModel
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Characters") },
                actions = {
                    // Filtro: al activarlo solo se muestran los personajes marcados como favoritos
                    FilterChip(
                        selected = uiState.showOnlyFavorites,
                        onClick = { viewModel.onIntent(CharactersIntent.FavoritesFilterClicked) },
                        label = { Text("Favoritos") },
                        leadingIcon = {
                            Icon(
                                imageVector = if (uiState.showOnlyFavorites) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = null,
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    )
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

        // Según el estado se decide qué layout mostrar
        when {
            uiState.isLoading -> LoadingLayout(
                onClick = { viewModel.onIntent(CharactersIntent.LoadingClicked) },
                modifier = contentModifier
            )

            uiState.hasError -> ErrorLayout(
                message = "Error al obtener listado de personajes.",
                onRetryClick = { viewModel.onIntent(CharactersIntent.RetryClicked) },
                modifier = contentModifier
            )

            // Pasa por ejemplo cuando el filtro está activo y no hay ningún favorito
            uiState.visibleCharacters.isEmpty() -> EmptyLayout(
                message = "No hay elementos a mostrar",
                modifier = contentModifier
            )

            else -> LazyColumn(modifier = contentModifier) {
                items(uiState.visibleCharacters) { character ->
                    CharacterListItem(
                        character = character,
                        onClick = { onCharacterClick(character.id) },
                        onFavoriteClick = { viewModel.onIntent(CharactersIntent.FavoriteClicked(character)) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun CharacterListItem(
    character: Character,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Uso de Coil para cargar la imagen circular
        AsyncImage(
            model = character.image,
            contentDescription = "Imagen de ${character.name}",
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        // weight(1f) hace que el texto ocupe el espacio libre y el corazón quede a la derecha
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = character.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${character.species} - ${character.status}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        FavoriteButton(isFavorite = character.isFavorite, onClick = onFavoriteClick)
    }
}