package com.example.tfgonitime.ui.screens.splashScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.ui.theme.Green
import com.example.tfgonitime.viewmodel.AuthViewModel
import com.example.tfgonitime.viewmodel.LanguageViewModel
import java.util.Locale

@Composable
fun SplashScreen(
    navHostController: NavHostController,
    authViewModel: AuthViewModel,
    languageViewModel: LanguageViewModel
) {

    val context = LocalContext.current

    // Cargar el idioma al iniciar la pantalla
    LaunchedEffect(Unit) {
        languageViewModel.loadLocale(context)
    }

    val locale by languageViewModel.locale

    val languages = listOf(
        "Español (España)" to Locale("es"),
        "Inglés (Reino Unido)" to Locale("en"),
        "Gallego" to Locale("gl")
    )

    // Encuentra el idioma actual
    var selectedLanguage by remember {
        mutableStateOf(
            languages.find { it.second.language == locale.language }?.first
                ?: languages[0].first
        )
    }
    val botonstart = when (locale.language) {
        "es" -> R.drawable.start_splash_btn_esp
        "en" -> R.drawable.start_splash_btn
        "gl" -> R.drawable.start_splash_btn_esp
        else -> R.drawable.start_splash_btn
    }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Green),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Logo at the top
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(280.dp)
                    .padding(top = 32.dp), // Adjust the top padding
                contentScale = ContentScale.Fit
            )

            // Splash art in the middle
            Image(
                painter = painterResource(id = R.drawable.splash_art),
                contentDescription = "Splash Art",
                modifier = Modifier
                    .size(300.dp),
                contentScale = ContentScale.Fit

            )
            Spacer(modifier = Modifier.height(70.dp))

            // Button at the bottom
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .size(width = 220.dp, height = 50.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = botonstart),

                    contentDescription = "Start Button",
                    modifier = Modifier
                        .size(500.dp)
                        .clickable {
                            navHostController.navigate("loadingScreen")

                        },
                    contentScale = ContentScale.Fit,


                    )
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
