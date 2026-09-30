package com.example.laboratorio7.navigation

import kotlinx.serialization.Serializable

// ---------- Grafo raíz ----------
@Serializable
object LoginDestination

// Pantalla principal que contiene el BottomNavigation
@Serializable
object MainDestination

// ---------- Nested graph de Characters ----------
@Serializable
object CharactersGraph

@Serializable
object CharactersDestination

@Serializable
data class CharacterDetailDestination(val characterId: Int)

// ---------- Nested graph de Locations ----------
@Serializable
object LocationsGraph

@Serializable
object LocationsDestination

// Solo viaja el ID, la pantalla de detalle busca lo demás en LocationDb
@Serializable
data class LocationDetailDestination(val locationId: Int)

// ---------- Profile (no necesita grafo propio, es una sola pantalla) ----------
@Serializable
object ProfileDestination
