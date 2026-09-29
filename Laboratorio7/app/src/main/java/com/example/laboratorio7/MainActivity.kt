package com.example.laboratorio7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.laboratorio7.navigation.LoginRoute
import com.example.laboratorio7.navigation.MainRoute
import com.example.laboratorio7.ui.screens.main.MainScreen
import com.example.laboratorio7.ui.theme.Laboratorio7Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Laboratorio7Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val rootNavController = rememberNavController()

                    // Este es el NavHost Raíz. Administra Login vs Pantalla Principal.
                    NavHost(
                        navController = rootNavController,
                        startDestination = LoginRoute
                    ) {

                        // Vista de Login
                        composable<LoginRoute> {
                            Button(onClick = {
                                // Al iniciar sesión, navegamos a MainRoute y destruimos LoginRoute
                                rootNavController.navigate(MainRoute) {
                                    popUpTo(LoginRoute) { inclusive = true }
                                }
                            }) {
                                Text("Iniciar Sesión (Simulado)")
                            }
                        }

                        // Pantalla principal que tiene el BottomNavigation
                        composable<MainRoute> {
                            MainScreen(
                                onLogout = {
                                    // REQUISITO: Al cerrar sesión, limpiar TODO el backstack
                                    rootNavController.navigate(LoginRoute) {
                                        // popUpTo(0) limpia completamente el historial
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}