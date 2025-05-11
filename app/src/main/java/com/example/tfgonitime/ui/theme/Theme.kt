package com.example.tfgonitime.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Green,
    onPrimary = White,
    secondary = Brown,
    onSecondary = Brown,
    tertiary = DarkBrown,
    background = DarkBrown,
    onBackground = Brown,
    surface = Color(0xFF2B2B2B), // Un fondo intermedio para superficies
    onSurface = White,
)

private val LightColorScheme = lightColorScheme(
    primary = Green,
    onPrimary = DarkBrown,//DarkBrown-White TEXTOS
    secondary = DarkBrown,//DarkBrown-Brown
    onSecondary = White,//White-Brown
    tertiary = Brown,//Brown-DarkBrown
    background = White, //White-DarkBrown
    onBackground = DarkBrown,// DarkBrown-Brown
    surface = Beige,
    onSurface = DarkBrown
)

@Composable
fun TfgOnitimeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Se usa el esquema de colores según el valor de darkTheme
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
