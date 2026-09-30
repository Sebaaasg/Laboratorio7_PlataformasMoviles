package com.example.laboratorio7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.laboratorio7.navigation.LoginDestination
import com.example.laboratorio7.navigation.MainDestination
import com.example.laboratorio7.ui.screens.login.LoginScreen
import com.example.laboratorio7.ui.screens.main.MainScreen
import com.example.laboratorio7.ui.theme.Laboratorio7Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Laboratorio7Theme {
                AppNavigation()
            }
        }
    }
}

// Grafo raíz: solo decide si se muestra el Login o la parte principal de la app
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = LoginDestination) {

        composable<LoginDestination> {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(MainDestination) {
                        // Se quita el Login del backstack para que "atrás" no regrese a él
                        popUpTo<LoginDestination> { inclusive = true }
                    }
                }
            )
        }

        composable<MainDestination> {
            MainScreen(
                onLogout = {
                    navController.navigate(LoginDestination) {
                        // graph.id es la raíz del grafo, entonces con inclusive = true
                        // se saca todo lo que había en el backstack y el Login queda solo.
                        // Si el usuario presiona "atrás" desde ahí, la app se cierra.
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
    }
}
