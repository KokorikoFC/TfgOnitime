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
    onTertiary = White,
    background = DarkBrown,
    onBackground = Brown,
    surface =  Green.copy(alpha = 0.50f), // Un fondo intermedio para superficies
    onSurface = Green,
)

private val LightColorScheme = lightColorScheme(
    primary = Green,
    onPrimary = DarkBrown,//DarkBrown-White TEXTOS
    secondary = DarkBrown,//DarkBrown-Brown
    onSecondary = White,//White-Brown
    tertiary = Brown,//Brown-DarkBrown
    onTertiary = Brown,//Brown-White
    background = White, //White-DarkBrown
    onBackground = DarkBrown,// DarkBrown-Brown
    surface = Green.copy(alpha = 0.25f),
    onSurface = Color(0xFFBED2B9)
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
