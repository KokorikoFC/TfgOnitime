package com.example.tfgonitime.ui.screens.splashScreen

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.ui.theme.Green
import com.example.tfgonitime.viewmodel.AuthViewModel
import com.example.tfgonitime.viewmodel.LanguageViewModel
import java.time.LocalDate
import java.util.Locale

@Composable
fun SplashScreen(
    navHostController: NavHostController,
    authViewModel: AuthViewModel,
    languageViewModel: LanguageViewModel
) {
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState(initial = false)
    val userId = authViewModel.userId.collectAsState(initial = null).value
    val context = LocalContext.current

    // Cargar idioma
    LaunchedEffect(Unit) {
        languageViewModel.loadLocale(context)
    }

    val locale by languageViewModel.locale
    val botonstart = when (locale.language) {
        "es" -> R.drawable.splash_start_btn_esp
        "en" -> R.drawable.splash_start_btn
        "gl" -> R.drawable.splash_start_btn_esp
        else -> R.drawable.splash_start_btn
    }

    // Redirección cuando `userId` está disponible
    LaunchedEffect(isAuthenticated, userId) {
        if (isAuthenticated == true && userId != null) {
            Log.d("SplashScreen", "Usuario autenticado, userId: $userId")
            if (shouldShowStreakScreen(context, userId)) {
                Log.d("SplashScreen", "Mostrando pantalla de streak para el usuario $userId")
                navHostController.navigate("streakScreen") {
                    popUpTo("splashScreen") { inclusive = true }
                }
            } else {
                Log.d("SplashScreen", "Redirigiendo al homeScreen")
                navHostController.navigate("homeScreen") {
                    popUpTo("splashScreen") { inclusive = true }
                }
            }
        }


    }


    // Pantalla Splash
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Green),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(280.dp)
                .padding(top = 32.dp),
            contentScale = ContentScale.Fit
        )

        Image(
            painter = painterResource(id = R.drawable.splash_mainart),
            contentDescription = "Splash Art",
            modifier = Modifier.size(300.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(70.dp))

        // Botón
        Image(
            painter = painterResource(id = botonstart),
            contentDescription = "Start Button",
            modifier = Modifier
                .size(width = 220.dp, height = 50.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    navHostController.navigate("loadingScreen")
                },
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(100.dp))
    }
}

fun shouldShowStreakScreen(context: Context, userId: String?): Boolean {
    if (userId == null) return false // Si no hay usuario logueado, no mostrar la pantalla

    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val lastOpenedDate = sharedPreferences.getString("last_streak_open_date_$userId", null)
    val today = LocalDate.now().toString() // La fecha de hoy

    Log.d("Streak", "userId: $userId, lastOpenedDate: $lastOpenedDate, today: $today")

    return if (lastOpenedDate == today) {
        false // Ya se abrió hoy
    } else {
        sharedPreferences.edit().putString("last_streak_open_date_$userId", today).apply()
        true // Se puede abrir la pantalla de streak
    }

}

