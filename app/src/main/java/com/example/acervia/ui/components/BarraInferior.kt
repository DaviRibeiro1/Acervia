package com.example.acervia.ui.components

import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.acervia.navigation.NavRoutes
import com.example.acervia.ui.theme.AcerviaTheme

sealed class DestinoBarraInferior(
    val rota: String,
    val rotulo: String,
    val iconeInativo: ImageVector,
    val iconeAtivo: ImageVector,
) {
    data object Inicio : DestinoBarraInferior(
        rota = NavRoutes.HOME,
        rotulo = "Início",
        iconeInativo = Icons.Outlined.Home,
        iconeAtivo = Icons.Filled.Home,
    )

    data object Catalogo : DestinoBarraInferior(
        rota = NavRoutes.CATALOGO,
        rotulo = "Catálogo",
        iconeInativo = Icons.Outlined.AutoStories,
        iconeAtivo = Icons.Outlined.AutoStories,
    )

    data object Reservas : DestinoBarraInferior(
        rota = NavRoutes.RESERVAS,
        rotulo = "Reservas",
        iconeInativo = Icons.Outlined.BookmarkBorder,
        iconeAtivo = Icons.Filled.Bookmark,
    )

    data object Ajustes : DestinoBarraInferior(
        rota = NavRoutes.AJUSTES,
        rotulo = "Ajustes",
        iconeInativo = Icons.Outlined.Settings,
        iconeAtivo = Icons.Filled.Settings,
    )

    companion object {
        val itens = listOf(Inicio, Catalogo, Reservas, Ajustes)
    }
}

@Composable
fun BarraInferior(
    rotaAtual: String,
    onNavigateTo: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
    ) {
        DestinoBarraInferior.itens.forEach { item ->
            val selecionado = rotaAtual == item.rota
            val icone = if (selecionado) item.iconeAtivo else item.iconeInativo

            NavigationBarItem(
                selected = selecionado,
                onClick = {
                    if (!selecionado) {
                        onNavigateTo(item.rota)
                    }
                },
                icon = {
                    Icon(
                        imageVector = icone,
                        contentDescription = item.rotulo,
                    )
                },
                label = {
                    Text(text = item.rotulo)
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}

@Preview(name = "Barra Inferior - Claro", showBackground = true)
@Composable
private fun BarraInferiorLightPreview() {
    AcerviaTheme(darkTheme = false) {
        BarraInferior(
            rotaAtual = NavRoutes.HOME,
            onNavigateTo = {},
        )
    }
}

@Preview(name = "Barra Inferior - Escuro", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun BarraInferiorDarkPreview() {
    AcerviaTheme(darkTheme = true) {
        BarraInferior(
            rotaAtual = NavRoutes.RESERVAS,
            onNavigateTo = {},
        )
    }
}
