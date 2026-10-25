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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.acervia.ui.screens.detalhes.DetalhesLivroScreen
import com.example.acervia.ui.screens.detalhes.DetalhesLivroViewModel
import com.example.acervia.ui.screens.detalhes.SuaReservaScreen
import com.example.acervia.ui.screens.detalhes.SuaReservaViewModel
import com.example.acervia.ui.screens.main.MainContainerScreen

object NavRoutes {
    const val SPLASH = "splash"
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val CODIGO = "codigo"
    const val FALTA_POUCO = "falta_pouco"
    const val MAIN_CONTAINER = "main_container?tab={tab}"
    const val HOME = "home"
    const val CATALOGO = "catalogo"
    const val RESERVAS = "reservas"
    const val AJUSTES = "ajustes"
    const val DETALHES_LIVRO = "detalhes_livro/{volumeId}"
    const val SUA_RESERVA = "sua_reserva/{reservaId}?confirmacao={confirmacao}"
}

@Composable
fun AcerviaNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = "main_container?tab=${NavRoutes.HOME}",
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(NavRoutes.SPLASH) {
            PlaceholderScreen(title = "Splash", onNext = { navController.navigate("main_container?tab=${NavRoutes.HOME}") })
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
            PlaceholderScreen(title = "Falta Pouco", onNext = { navController.navigate("main_container?tab=${NavRoutes.HOME}") })
        }
        composable(
            route = NavRoutes.MAIN_CONTAINER,
            arguments = listOf(
                navArgument("tab") {
                    type = NavType.StringType
                    defaultValue = NavRoutes.HOME
                },
            ),
        ) { backStackEntry ->
            val initialTab = backStackEntry.arguments?.getString("tab") ?: NavRoutes.HOME
            MainContainerScreen(
                rootNavController = navController,
                initialTab = initialTab,
            )
        }
        composable(NavRoutes.DETALHES_LIVRO) {
            val viewModel: DetalhesLivroViewModel = hiltViewModel()
            DetalhesLivroScreen(
                viewModel = viewModel,
                onVoltarClick = { navController.popBackStack() },
                onReservaConfirmada = { reservaId ->
                    navController.navigate("sua_reserva/$reservaId?confirmacao=true")
                },
            )
        }
        composable(
            route = NavRoutes.SUA_RESERVA,
            arguments = listOf(
                navArgument("reservaId") { type = NavType.StringType },
                navArgument("confirmacao") {
                    type = NavType.BoolType
                    defaultValue = false
                },
            ),
        ) {
            val viewModel: SuaReservaViewModel = hiltViewModel()
            SuaReservaScreen(
                viewModel = viewModel,
                onVoltarClick = { navController.popBackStack() },
                onVerMinhasReservasClick = {
                    navController.navigate("main_container?tab=${NavRoutes.RESERVAS}") {
                        popUpTo("main_container?tab=${NavRoutes.HOME}") { inclusive = true }
                    }
                },
                onLivroClick = { volumeId ->
                    navController.navigate("detalhes_livro/$volumeId")
                },
            )
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
