package com.example.laboratorio7.ui.screens.locationdetails

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.laboratorio7.data.Location
import com.example.laboratorio7.ui.components.ErrorLayout
import com.example.laboratorio7.ui.components.FavoriteButton
import com.example.laboratorio7.ui.components.LoadingLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDetailsScreen(
    onBackClick: () -> Unit,
    viewModel: LocationDetailsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val location = uiState.data

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Location details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                actions = {
                    // Mismo corazón que en la lista, así el detalle muestra si es favorita
                    if (location != null) {
                        FavoriteButton(
                            isFavorite = location.isFavorite,
                            onClick = { viewModel.onIntent(LocationDetailsIntent.FavoriteClicked) }
                        )
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
                onClick = { viewModel.onIntent(LocationDetailsIntent.LoadingClicked) },
                modifier = contentModifier
            )

            uiState.hasError -> ErrorLayout(
                message = "Error al obtener la ubicación.",
                onRetryClick = { viewModel.onIntent(LocationDetailsIntent.RetryClicked) },
                modifier = contentModifier
            )

            location != null -> LocationDetailsContent(
                location = location,
                modifier = contentModifier
            )
        }
    }
}

@Composable
private fun LocationDetailsContent(location: Location, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = 40.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = location.name,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        LocationInfoRow(label = "ID:", value = location.id.toString())
        Spacer(modifier = Modifier.height(12.dp))
        LocationInfoRow(label = "Type:", value = location.type)
        Spacer(modifier = Modifier.height(12.dp))
        LocationInfoRow(label = "Dimension:", value = location.dimension)
    }
}

// Etiqueta a la izquierda y valor a la derecha, igual que en el detalle de personajes
@Composable
private fun LocationInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}