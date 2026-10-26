package com.example.acervia.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.acervia.ui.screens.auth.CodigoAcessoScreen
import com.example.acervia.ui.screens.auth.CodigoAcessoViewModel
import com.example.acervia.ui.screens.auth.FaltaPoucoScreen
import com.example.acervia.ui.screens.auth.FaltaPoucoViewModel
import com.example.acervia.ui.screens.auth.LoginScreen
import com.example.acervia.ui.screens.auth.LoginViewModel
import com.example.acervia.ui.screens.auth.SplashScreen
import com.example.acervia.ui.screens.auth.SplashViewModel
import com.example.acervia.ui.screens.auth.WelcomeScreen
import com.example.acervia.ui.screens.detalhes.DetalhesLivroScreen
import com.example.acervia.ui.screens.detalhes.DetalhesLivroViewModel
import com.example.acervia.ui.screens.detalhes.SuaReservaScreen
import com.example.acervia.ui.screens.detalhes.SuaReservaViewModel
import com.example.acervia.ui.screens.main.MainContainerScreen

object NavRoutes {
    const val SPLASH = "splash"
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val CODIGO = "codigo?email={email}"
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
    startDestination: String = NavRoutes.SPLASH,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(NavRoutes.SPLASH) {
            val viewModel: SplashViewModel = hiltViewModel()
            SplashScreen(
                viewModel = viewModel,
                onNavegar = { rota ->
                    navController.navigate(rota) {
                        popUpTo(NavRoutes.SPLASH) { inclusive = true }
                    }
                },
            )
        }
        composable(NavRoutes.WELCOME) {
            WelcomeScreen(
                onComecarClick = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.WELCOME) { inclusive = true }
                    }
                },
            )
        }
        composable(NavRoutes.LOGIN) {
            val viewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = viewModel,
                onEnviarCodigoSucesso = { email ->
                    navController.navigate("codigo?email=$email")
                },
                onGoogleLoginSucesso = { contaNova ->
                    val destino = if (contaNova) NavRoutes.FALTA_POUCO else "main_container?tab=${NavRoutes.HOME}"
                    navController.navigate(destino) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                },
            )
        }
        composable(
            route = NavRoutes.CODIGO,
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                    defaultValue = ""
                },
            ),
        ) {
            val viewModel: CodigoAcessoViewModel = hiltViewModel()
            CodigoAcessoScreen(
                viewModel = viewModel,
                onVoltarClick = { navController.popBackStack() },
                onLoginSucesso = { contaNova ->
                    val destino = if (contaNova) NavRoutes.FALTA_POUCO else "main_container?tab=${NavRoutes.HOME}"
                    navController.navigate(destino) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                },
            )
        }
        composable(NavRoutes.FALTA_POUCO) {
            val viewModel: FaltaPoucoViewModel = hiltViewModel()
            FaltaPoucoScreen(
                viewModel = viewModel,
                onVoltarClick = { navController.popBackStack() },
                onCadastroConcluido = {
                    navController.navigate("main_container?tab=${NavRoutes.HOME}") {
                        popUpTo(NavRoutes.WELCOME) { inclusive = true }
                    }
                },
            )
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
