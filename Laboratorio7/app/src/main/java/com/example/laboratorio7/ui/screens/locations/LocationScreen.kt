package com.example.laboratorio7.ui.screens.locations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.laboratorio7.data.Location
import com.example.laboratorio7.data.LocationDb

@Composable
fun LocationsScreen(onLocationClick: (Int) -> Unit) {
    // Obtenemos la lista de locaciones de la base de datos simulada
    val locations = remember { LocationDb().getAllLocations() }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(locations) { location ->
            LocationItem(
                location = location,
                // Al hacer click, pasamos solo el ID hacia arriba (hoisting)
                onClick = { onLocationClick(location.id) }
            )
            HorizontalDivider() // Línea separadora sutil
        }
    }
}

@Composable
fun LocationItem(location: Location, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Text(
            text = location.name,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = location.type,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}