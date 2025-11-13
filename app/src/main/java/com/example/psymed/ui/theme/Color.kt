package com.example.psymed.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset

object PsyMedColors {
    val Primary = Color(0xFF308B82)
    val PrimaryMedium = Color(0xFF69BFB6)
    val PrimaryLight = Color(0xFFE5FFFC)
    val PrimaryLighter = Color(0xFFE7FFFD)
    val PrimaryLightest = Color(0xFFE9FFFF)

    val Background = Color(0xFFF9F9F9)
    val CardBackground = Color(0xFFFFFFFF)

    val TextPrimary = Color(0xFF2D3748)
    val TextSecondary = Color(0xFF718096)
    val TextLight = Color(0xFFA0AEC0)

    val Success = Color(0xFF48BB78)
    val Error = Color(0xFFF56565)
    val Warning = Color(0xFFED8936)
    val Info = PrimaryMedium

    val Shadow = Primary.copy(alpha = 0.15f)
    val Divider = Color(0xFFE2E8F0)

    private val gradientEnd = Offset(1000f, 1000f)

    val PrimaryGradient: Brush
        get() = Brush.linearGradient(
            colors = listOf(Primary, PrimaryMedium),
            start = Offset.Zero,
            end = gradientEnd
        )

    val AccentGradient: Brush
        get() = Brush.linearGradient(
            colors = listOf(PrimaryMedium, PrimaryLight),
            start = Offset.Zero,
            end = gradientEnd
        )

    val LightGradient: Brush
        get() = Brush.linearGradient(
            colors = listOf(PrimaryLight, PrimaryLightest),
            start = Offset.Zero,
            end = gradientEnd
        )
}