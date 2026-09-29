package com.example.laboratorio7.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(onLogoutClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Se usa un ícono predeterminado para simplificar
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Imagen de perfil",
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        ProfileInfoRow(label = "Nombre:", value = "Estuardo Sebastian García de León")
        Spacer(modifier = Modifier.height(8.dp))
        ProfileInfoRow(label = "Carné:", value = "25057")

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedButton(onClick = onLogoutClick) {
            Text("Cerrar sesión")
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}