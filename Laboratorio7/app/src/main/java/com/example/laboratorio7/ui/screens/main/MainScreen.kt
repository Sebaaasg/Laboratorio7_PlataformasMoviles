package com.example.laboratorio7.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.toRoute
import com.example.laboratorio7.navigation.*
import com.example.laboratorio7.ui.screens.locations.LocationDetailsScreen
import com.example.laboratorio7.ui.screens.locations.LocationsScreen
import com.example.laboratorio7.ui.screens.profile.ProfileScreen
import androidx.navigation.NavDestination.Companion.hasRoute

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = bottomNavController)
        }
    ) { paddingValues ->
        // Este NavHost maneja únicamente la navegación de las 3 opciones inferiores
        NavHost(
            navController = bottomNavController,
            startDestination = CharactersGraph,
            modifier = Modifier.padding(paddingValues)
        ) {

            // 1. Nested Graph de Characters (Lab 7)
            navigation<CharactersGraph>(startDestination = CharactersListRoute) {
                composable<CharactersListRoute> {
                    // Aquí mandas a llamar al composable de Characters que ya tenías en el Lab 7
                    // CharactersScreen(...)
                    Text("Vista de Characters de Lab 7") // Placeholder
                }
            }

            // 2. Nested Graph de Locations
            navigation<LocationsGraph>(startDestination = LocationsListRoute) {
                composable<LocationsListRoute> {
                    LocationsScreen(
                        onLocationClick = { locationId ->
                            bottomNavController.navigate(LocationDetailsRoute(id = locationId))
                        }
                    )
                }

                composable<LocationDetailsRoute> { backStackEntry ->
                    // Extraemos el parámetro ID de manera segura usando toRoute()
                    val detailsRoute = backStackEntry.toRoute<LocationDetailsRoute>()
                    LocationDetailsScreen(
                        locationId = detailsRoute.id,
                        onBackClick = { bottomNavController.popBackStack() }
                    )
                }
            }

            // 3. Ventana individual de Perfil
            composable<ProfileRoute> {
                ProfileScreen(onLogoutClick = onLogout)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        // Item: Characters
        val isCharactersSelected = currentDestination?.hierarchy?.any { it.hasRoute(CharactersGraph::class) } == true
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Characters") },
            label = { Text("Characters") },
            selected = isCharactersSelected,
            onClick = {
                navController.navigate(CharactersGraph) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        // Item: Locations
        val isLocationsSelected = currentDestination?.hierarchy?.any { it.hasRoute(LocationsGraph::class) } == true
        NavigationBarItem(
            icon = { Icon(Icons.Default.LocationOn, contentDescription = "Locations") },
            label = { Text("Locations") },
            selected = isLocationsSelected,
            onClick = {
                navController.navigate(LocationsGraph) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        // Item: Profile
        val isProfileSelected = currentDestination?.hierarchy?.any { it.hasRoute(ProfileRoute::class) } == true
        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = isProfileSelected,
            onClick = {
                navController.navigate(ProfileRoute) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}