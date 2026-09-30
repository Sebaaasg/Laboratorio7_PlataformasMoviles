package com.example.laboratorio7.ui.screens.main

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.laboratorio7.navigation.CharacterDetailDestination
import com.example.laboratorio7.navigation.CharactersDestination
import com.example.laboratorio7.navigation.CharactersGraph
import com.example.laboratorio7.navigation.LocationDetailDestination
import com.example.laboratorio7.navigation.LocationsDestination
import com.example.laboratorio7.navigation.LocationsGraph
import com.example.laboratorio7.navigation.ProfileDestination
import com.example.laboratorio7.ui.screens.characterdetail.CharacterDetailScreen
import com.example.laboratorio7.ui.screens.characters.CharactersScreen
import com.example.laboratorio7.ui.screens.locationdetails.LocationDetailsScreen
import com.example.laboratorio7.ui.screens.locations.LocationsScreen
import com.example.laboratorio7.ui.screens.profile.ProfileScreen

// Cada opción del BottomNavigation: texto, ícono y a dónde lleva
private data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val destination: Any
)

private val bottomNavItems = listOf(
    BottomNavItem("Characters", Icons.Filled.People, CharactersGraph),
    BottomNavItem("Locations", Icons.Filled.Public, LocationsGraph),
    BottomNavItem("Profile", Icons.Filled.Person, ProfileDestination)
)

@Composable
fun MainScreen(onLogout: () -> Unit) {
    // NavController propio para las pestañas, separado del de Login/Main
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = CharactersGraph,
            // consumeWindowInsets evita que los TopAppBar de cada pantalla
            // vuelvan a sumar el espacio de la barra de estado
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {

            // Nested graph de Characters: lista y detalle del Lab 7
            navigation<CharactersGraph>(startDestination = CharactersDestination) {
                composable<CharactersDestination> {
                    CharactersScreen(
                        onCharacterClick = { id ->
                            navController.navigate(CharacterDetailDestination(characterId = id))
                        }
                    )
                }

                composable<CharacterDetailDestination> { backStackEntry ->
                    val route = backStackEntry.toRoute<CharacterDetailDestination>()
                    CharacterDetailScreen(
                        characterId = route.characterId,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }

            // Nested graph de Locations: lista y detalle
            navigation<LocationsGraph>(startDestination = LocationsDestination) {
                composable<LocationsDestination> {
                    LocationsScreen(
                        onLocationClick = { id ->
                            // Solo se manda el ID a la pantalla de detalle
                            navController.navigate(LocationDetailDestination(locationId = id))
                        }
                    )
                }

                composable<LocationDetailDestination> { backStackEntry ->
                    val route = backStackEntry.toRoute<LocationDetailDestination>()
                    LocationDetailsScreen(
                        locationId = route.locationId,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }

            // Profile es una sola pantalla, no necesita nested graph
            composable<ProfileDestination> {
                ProfileScreen(onLogoutClick = onLogout)
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(containerColor = MaterialTheme.colorScheme.primary) {
        bottomNavItems.forEach { item ->
            // hierarchy incluye la pantalla actual y los grafos que la contienen,
            // así "Locations" sigue marcado aunque estemos en el detalle
            val selected = currentDestination?.hierarchy?.any {
                it.hasRoute(item.destination::class)
            } == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.destination) {
                        // Regresa al inicio del grafo para no ir apilando pestañas,
                        // pero guarda el estado de la pestaña que se deja
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // No crea otra copia si ya estamos en esa pestaña
                        launchSingleTop = true
                        // Al volver a una pestaña, recupera donde se había quedado
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = {
                    Text(
                        text = item.label,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    indicatorColor = MaterialTheme.colorScheme.secondary,
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedTextColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}
