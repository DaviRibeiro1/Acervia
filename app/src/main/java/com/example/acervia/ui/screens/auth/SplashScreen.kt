package com.example.acervia.ui.screens.auth

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.acervia.ui.theme.AcerviaTheme
import com.example.acervia.ui.theme.Space

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavegar: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val destinoState by viewModel.destino.collectAsState()

    LaunchedEffect(destinoState) {
        when (val estado = destinoState) {
            is SplashDestino.Concluido -> onNavegar(estado.rota)
            is SplashDestino.EmAndamento -> {}
        }
    }

    SplashContent(modifier = modifier)
}

@Composable
fun SplashContent(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Space.s3),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp),
            )
            Text(
                text = "Acervia",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.semantics { heading() },
            )
        }
    }
}

@Preview(name = "Splash - Claro", showBackground = true)
@Composable
private fun SplashScreenLightPreview() {
    AcerviaTheme(darkTheme = false) {
        SplashContent()
    }
}

@Preview(name = "Splash - Escuro", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun SplashScreenDarkPreview() {
    AcerviaTheme(darkTheme = true) {
        SplashContent()
    }
}
