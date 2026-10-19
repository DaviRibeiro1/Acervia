package com.example.acervia.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object NavRoutes {
    const val SPLASH = "splash"
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val CODIGO = "codigo"
    const val FALTA_POUCO = "falta_pouco"
    const val HOME = "home"
    const val CATALOGO = "catalogo"
    const val RESERVAS = "reservas"
    const val AJUSTES = "ajustes"
    const val DETALHES_LIVRO = "detalhes_livro/{volumeId}"
    const val SUA_RESERVA = "sua_reserva/{reservaId}"
}

@Composable
fun AcerviaNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavRoutes.SPLASH,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(NavRoutes.SPLASH) {
            PlaceholderScreen(title = "Splash", onNext = { navController.navigate(NavRoutes.WELCOME) })
        }
        composable(NavRoutes.WELCOME) {
            PlaceholderScreen(title = "Welcome", onNext = { navController.navigate(NavRoutes.LOGIN) })
        }
        composable(NavRoutes.LOGIN) {
            PlaceholderScreen(title = "Login", onNext = { navController.navigate(NavRoutes.CODIGO) })
        }
        composable(NavRoutes.CODIGO) {
            PlaceholderScreen(title = "Código de Acesso", onNext = { navController.navigate(NavRoutes.FALTA_POUCO) })
        }
        composable(NavRoutes.FALTA_POUCO) {
            PlaceholderScreen(title = "Falta Pouco", onNext = { navController.navigate(NavRoutes.HOME) })
        }
        composable(NavRoutes.HOME) {
            PlaceholderScreen(title = "Home", onNext = { navController.navigate(NavRoutes.CATALOGO) })
        }
        composable(NavRoutes.CATALOGO) {
            PlaceholderScreen(title = "Catálogo", onNext = { navController.navigate(NavRoutes.RESERVAS) })
        }
        composable(NavRoutes.RESERVAS) {
            PlaceholderScreen(title = "Reservas", onNext = { navController.navigate(NavRoutes.AJUSTES) })
        }
        composable(NavRoutes.AJUSTES) {
            PlaceholderScreen(title = "Ajustes", onNext = { navController.navigate(NavRoutes.HOME) })
        }
    }
}

@Composable
private fun PlaceholderScreen(
    title: String,
    onNext: () -> Unit,
) {
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Acervia - $title",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
