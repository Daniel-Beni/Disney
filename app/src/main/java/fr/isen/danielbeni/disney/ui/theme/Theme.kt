package fr.isen.danielbeni.disney.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Thème Disney — on désactive Material You pour imposer notre palette.
 * Bleu nuit profond + accents dorés = ambiance Disney+ / cinéma.
 */

private val DarkColorScheme = darkColorScheme(
    primary = DisneyGold,
    secondary = DisneyRoyalBlue,
    tertiary = DisneyLightGold,
    background = DisneyDarkBlue,
    surface = DisneySurfaceDark,
    onPrimary = DisneyDarkBlue,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    error = DisneyError,
    onError = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = DisneyRoyalBlue,
    secondary = DisneyGold,
    tertiary = DisneyDarkBlue,
    background = DisneyCream,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = DisneyDarkBlue,
    onBackground = DisneyDarkBlue,
    onSurface = DisneyDarkBlue,
    error = Color(0xFFB00020),
    onError = Color.White
)

@Composable
fun DisneyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}