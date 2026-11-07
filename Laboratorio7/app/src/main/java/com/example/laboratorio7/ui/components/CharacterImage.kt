package com.example.laboratorio7.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage

// Foto circular del personaje, se usa en la lista y en el detalle
@Composable
fun CharacterImage(image: String, name: String, size: Dp, modifier: Modifier = Modifier) {
    val imageModifier = modifier
        .size(size)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surfaceVariant)

    if (image.isBlank()) {
        // Los personajes creados en la app no tienen foto, se muestra un ícono genérico
        Box(modifier = imageModifier, contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Sin imagen",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(size / 2)
            )
        }
    } else {
        AsyncImage(
            model = image,
            contentDescription = "Imagen de $name",
            modifier = imageModifier,
            contentScale = ContentScale.Crop
        )
    }
}
