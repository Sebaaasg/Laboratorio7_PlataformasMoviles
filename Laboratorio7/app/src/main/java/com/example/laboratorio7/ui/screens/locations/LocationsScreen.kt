package com.example.laboratorio7.ui.screens.locations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.laboratorio7.data.Location
import com.example.laboratorio7.ui.components.EmptyLayout
import com.example.laboratorio7.ui.components.ErrorLayout
import com.example.laboratorio7.ui.components.FavoriteButton
import com.example.laboratorio7.ui.components.LoadingLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationsScreen(
    onLocationClick: (Int) -> Unit,
    viewModel: LocationsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Locations") },
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
                onClick = { viewModel.onIntent(LocationsIntent.LoadingClicked) },
                modifier = contentModifier
            )

            uiState.hasError -> ErrorLayout(
                message = "Error al obtener listado de ubicaciones.",
                onRetryClick = { viewModel.onIntent(LocationsIntent.RetryClicked) },
                modifier = contentModifier
            )

            else -> Column(modifier = contentModifier) {
                LocationFilterRow(
                    selectedFilter = uiState.filter,
                    onFilterClick = { viewModel.onIntent(LocationsIntent.FilterSelected(it)) }
                )

                if (uiState.visibleLocations.isEmpty()) {
                    EmptyLayout(
                        message = "No hay elementos a mostrar",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(uiState.visibleLocations, key = { it.id }) { location ->
                            // Al hacer click solo se manda el ID hacia arriba
                            LocationListItem(
                                location = location,
                                onClick = { onLocationClick(location.id) },
                                onFavoriteClick = { viewModel.onIntent(LocationsIntent.FavoriteClicked(location)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Un chip por cada opción del enum, solo uno puede estar seleccionado a la vez
@Composable
private fun LocationFilterRow(
    selectedFilter: LocationFilter,
    onFilterClick: (LocationFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LocationFilter.entries.forEach { filter ->
            FilterChip(
                selected = filter == selectedFilter,
                onClick = { onFilterClick(filter) },
                label = { Text(filter.label) }
            )
        }
    }
}

// Cada item muestra el nombre, abajo el tipo y a la derecha el corazón
@Composable
private fun LocationListItem(
    location: Location,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(start = 24.dp, end = 8.dp, top = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = location.name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = location.type,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }

        FavoriteButton(isFavorite = location.isFavorite, onClick = onFavoriteClick)
    }
}
