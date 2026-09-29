package com.example.laboratorio7.navigation

import kotlinx.serialization.Serializable

//  Rutas Raíz
@Serializable
object LoginRoute

@Serializable
object MainRoute // La vista que contendrá el BottomNavigation

// --- Nested Graph para Characters ---
@Serializable
object CharactersGraph

@Serializable
object CharactersListRoute // Pantalla de lista de personajes

// --- Nested Graph para Locations ---
@Serializable
object LocationsGraph

@Serializable
object LocationsListRoute // Pantalla de lista de locaciones

// se pasa únicamente el ID como parámetro
@Serializable
data class LocationDetailsRoute(val id: Int)

// --- Perfil ---
@Serializable
object ProfileRoute