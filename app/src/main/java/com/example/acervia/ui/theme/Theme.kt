package com.example.acervia.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun AcerviaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val extra = if (darkTheme) DarkExtra else LightExtra
    CompositionLocalProvider(LocalExtraColors provides extra) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = AppTypography,
            shapes = AppShapes,
            content = content,
        )
    }
}

object AppTheme {
    val extra: ExtraColors
        @Composable get() = LocalExtraColors.current
}
