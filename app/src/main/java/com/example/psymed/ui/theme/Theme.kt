package com.example.psymed.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PsyMedColors.Primary,
    onPrimary = Color.White,
    primaryContainer = PsyMedColors.PrimaryMedium,
    onPrimaryContainer = Color.White,
    secondary = PsyMedColors.PrimaryMedium,
    onSecondary = Color.White,
    secondaryContainer = PsyMedColors.PrimaryLight,
    onSecondaryContainer = PsyMedColors.TextPrimary,
    tertiary = PsyMedColors.Primary,
    onTertiary = Color.White,
    background = PsyMedColors.Background,
    onBackground = PsyMedColors.TextPrimary,
    surface = PsyMedColors.CardBackground,
    onSurface = PsyMedColors.TextPrimary,
    surfaceVariant = PsyMedColors.PrimaryLight,
    onSurfaceVariant = PsyMedColors.TextSecondary,
    outline = PsyMedColors.Divider,
    error = PsyMedColors.Error,
    onError = Color.White,
    errorContainer = PsyMedColors.Error.copy(alpha = 0.12f),
    onErrorContainer = PsyMedColors.Error
)

private val DarkColorScheme = darkColorScheme(
    primary = PsyMedColors.Primary,
    onPrimary = Color.White,
    primaryContainer = PsyMedColors.Primary.copy(alpha = 0.7f),
    onPrimaryContainer = Color.White,
    secondary = PsyMedColors.PrimaryMedium,
    onSecondary = Color.White,
    background = Color(0xFF101316),
    onBackground = Color.White,
    surface = Color(0xFF182026),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF24303A),
    onSurfaceVariant = PsyMedColors.PrimaryLightest,
    outline = PsyMedColors.PrimaryLight,
    error = PsyMedColors.Error,
    onError = Color.White
)

@Composable
fun PsymedTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}