package com.example.acervia.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LightColors = lightColorScheme(
    primary = Color(0xFF1E3A5F),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDCE6F5),
    onPrimaryContainer = Color(0xFF0F2744),
    background = Color(0xFFF7F8FA),
    onBackground = Color(0xFF1F2937),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1F2937),
    onSurfaceVariant = Color(0xFF5B6472), // color.text-secondary
    outline = Color(0xFF6B7686),
    outlineVariant = Color(0xFFD5DAE1), // color.divider
    error = Color(0xFFB42318),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFEE4E2),
    onErrorContainer = Color(0xFFB42318),
)

val DarkColors = darkColorScheme(
    primary = Color(0xFF9DB8E6),
    onPrimary = Color(0xFF0F2744),
    primaryContainer = Color(0xFF22364F),
    onPrimaryContainer = Color(0xFFDCE6F5),
    background = Color(0xFF0F1620),
    onBackground = Color(0xFFE6EAF0),
    surface = Color(0xFF172131),
    onSurface = Color(0xFFE6EAF0),
    onSurfaceVariant = Color(0xFFA3ADBB),
    outline = Color(0xFF7B8798),
    outlineVariant = Color(0xFF2A3547),
    error = Color(0xFFF97066),
    onError = Color(0xFF0F1620),
    errorContainer = Color(0xFF3F1A14),
    onErrorContainer = Color(0xFFF97066),
)

// Tokens adicionais que o Material 3 não contempla nativamente.
@Immutable
data class ExtraColors(
    val success: Color,
    val successContainer: Color,
    val accent: Color,
    val accentContainer: Color,
    val onAccentContainer: Color,
    val focus: Color,
    val disabledContainer: Color,
    val disabledContent: Color,
)

val LightExtra = ExtraColors(
    success = Color(0xFF067647),
    successContainer = Color(0xFFD1FADF),
    accent = Color(0xFFB54708),
    accentContainer = Color(0xFFFEF0C7),
    onAccentContainer = Color(0xFF93370D),
    focus = Color(0xFF1D4FBF),
    disabledContainer = Color(0xFFE5E8ED),
    disabledContent = Color(0xFF8A94A3),
)

val DarkExtra = ExtraColors(
    success = Color(0xFF47CD89),
    successContainer = Color(0xFF0B3B26),
    accent = Color(0xFFF79009),
    accentContainer = Color(0xFF3D2506),
    onAccentContainer = Color(0xFFFDB022),
    focus = Color(0xFF84ADFF),
    disabledContainer = Color(0xFF243044),
    disabledContent = Color(0xFF5F6B7C),
)

val LocalExtraColors = staticCompositionLocalOf { LightExtra }
