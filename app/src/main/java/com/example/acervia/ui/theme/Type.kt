package com.example.acervia.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val InterFontFamily = FontFamily.SansSerif

private fun style(size: Int, line: Int, weight: FontWeight) = TextStyle(
    fontFamily = InterFontFamily,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = line.sp,
    letterSpacing = 0.sp,
)

val AppTypography = Typography(
    headlineLarge = style(32, 40, FontWeight.Bold), // type.display
    headlineMedium = style(28, 36, FontWeight.Bold), // type.title
    titleLarge = style(22, 28, FontWeight.SemiBold), // type.subtitle
    bodyLarge = style(16, 24, FontWeight.Normal), // type.body
    bodyMedium = style(14, 20, FontWeight.Normal), // type.body-small
    labelLarge = style(14, 20, FontWeight.Medium), // type.label
    bodySmall = style(12, 16, FontWeight.Normal), // type.caption
)

object AppTextStyles {
    val button = style(16, 24, FontWeight.SemiBold) // type.button
    val otp = style(24, 32, FontWeight.SemiBold) // type.otp
}
