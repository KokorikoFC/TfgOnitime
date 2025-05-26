package com.example.tfgonitime.ui.screens.splashScreen

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
import com.example.tfgonitime.data.repository.UserRepository
import com.example.tfgonitime.ui.theme.Green
import com.example.tfgonitime.viewmodel.AuthViewModel
import com.example.tfgonitime.viewmodel.LanguageViewModel
import com.example.tfgonitime.viewmodel.StreakViewModel

@Composable
fun SplashScreen(
    navHostController: NavHostController,
    authViewModel: AuthViewModel,
    languageViewModel: LanguageViewModel
) {
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState(initial = false)
    val userId = authViewModel.userId.collectAsState(initial = null).value
    val context = LocalContext.current
    val streakViewModel = StreakViewModel()
    val userRepository = UserRepository()

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

            userRepository.updateActiveDay(userId)

            // Comprobar si el usuario ha iniciado sesión hoy
            if (streakViewModel.shouldShowStreakScreen(userId)) {
                Log.d("SplashScreen", "Mostrando pantalla de streak para el usuario $userId")
                navHostController.navigate("streakScreen") {
                    popUpTo("splashScreen") { inclusive = true }
                }
            } else {
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

