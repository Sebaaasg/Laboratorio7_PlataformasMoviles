package com.example.laboratorio7.ui.screens.locations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.laboratorio7.data.Location
import com.example.laboratorio7.ui.components.ErrorLayout
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

            else -> LazyColumn(modifier = contentModifier) {
                items(uiState.data) { location ->
                    // Al hacer click solo se manda el ID hacia arriba
                    LocationListItem(location = location, onClick = { onLocationClick(location.id) })
                }
            }
        }
    }
}

// Cada item muestra el nombre y abajo el tipo
@Composable
private fun LocationListItem(location: Location, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
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
}