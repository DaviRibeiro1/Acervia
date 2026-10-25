package com.example.acervia.ui.screens.main

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.acervia.navigation.NavRoutes
import com.example.acervia.ui.components.BarraInferior
import com.example.acervia.ui.screens.catalogo.CatalogoScreen
import com.example.acervia.ui.screens.catalogo.CatalogoViewModel
import com.example.acervia.ui.screens.reservas.ReservasScreen
import com.example.acervia.ui.screens.reservas.ReservasViewModel
import com.example.acervia.ui.theme.AcerviaTheme

@Composable
fun MainContainerScreen(
    rootNavController: NavHostController = rememberNavController(),
    tabNavController: NavHostController = rememberNavController(),
    initialTab: String = NavRoutes.HOME,
) {
    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: initialTab

    Scaffold(
        bottomBar = {
            BarraInferior(
                rotaAtual = currentRoute,
                onNavigateTo = { targetRoute ->
                    tabNavController.navigate(targetRoute) {
                        popUpTo(tabNavController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        },
    ) { innerPadding ->
        NavHost(
            navController = tabNavController,
            startDestination = initialTab,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(NavRoutes.HOME) {
                TabPlaceholderScreen(
                    title = "Início",
                    onOpenBookDetails = { volumeId ->
                        rootNavController.navigate("detalhes_livro/$volumeId")
                    },
                )
            }
            composable(NavRoutes.CATALOGO) {
                val viewModel: CatalogoViewModel = hiltViewModel()
                CatalogoScreen(
                    viewModel = viewModel,
                    onLivroClick = { volumeId ->
                        rootNavController.navigate("detalhes_livro/$volumeId")
                    },
                )
            }
            composable(NavRoutes.RESERVAS) {
                val viewModel: ReservasViewModel = hiltViewModel()
                ReservasScreen(
                    viewModel = viewModel,
                    onReservaClick = { reservaId ->
                        rootNavController.navigate("sua_reserva/$reservaId")
                    },
                    onExplorarCatalogoClick = {
                        tabNavController.navigate(NavRoutes.CATALOGO) {
                            popUpTo(tabNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
            composable(NavRoutes.AJUSTES) {
                TabPlaceholderScreen(
                    title = "Ajustes",
                    onOpenBookDetails = { volumeId ->
                        rootNavController.navigate("detalhes_livro/$volumeId")
                    },
                )
            }
        }
    }
}

@Composable
private fun TabPlaceholderScreen(
    title: String,
    onOpenBookDetails: (String) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Aba: $title",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Preview(name = "Main Container - Claro", showBackground = true)
@Composable
private fun MainContainerScreenLightPreview() {
    AcerviaTheme(darkTheme = false) {
        MainContainerScreen()
    }
}

@Preview(name = "Main Container - Escuro", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun MainContainerScreenDarkPreview() {
    AcerviaTheme(darkTheme = true) {
        MainContainerScreen()
    }
}
