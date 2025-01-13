package com.example.tfgonitime.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.ui.components.CustomBottomNavBar
import com.example.tfgonitime.viewmodel.AuthViewModel
import com.example.tfgonitime.viewmodel.LanguageViewModel
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale
import android.content.res.Configuration
import com.example.tfgonitime.data.repository.LanguageManager


@Composable
fun HomeScreen(navHostController: NavHostController, authViewModel: AuthViewModel, languageViewModel: LanguageViewModel) {

    val context = LocalContext.current
    val locale by languageViewModel.locale

    val configuration = LocalConfiguration.current
    val updatedConfiguration = configuration.apply {
        setLocale(locale)
    }

    CompositionLocalProvider(LocalConfiguration provides updatedConfiguration) {
        Scaffold(
            containerColor = Color.White,
            bottomBar = { CustomBottomNavBar(navHostController) },
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Selecciona el idioma", style = MaterialTheme.typography.bodyMedium)

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = stringResource(R.string.forgot_password))

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(onClick = {
                            languageViewModel.setLocale(Locale("es"))
                            LanguageManager.setLocale(context, Locale("es"))
                        }) {
                            Text(text = "Español")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(onClick = {
                            languageViewModel.setLocale(Locale("en"))
                            LanguageManager.setLocale(context, Locale("en"))
                        }) {
                            Text(text = "English")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(onClick = {
                            languageViewModel.setLocale(Locale("gl"))
                            LanguageManager.setLocale(context, Locale("gl"))
                        }) {
                            Text(text = "Galego")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(onClick = {
                            authViewModel.logout {
                                navHostController.navigate("loginScreen") // Navega a la pantalla de login
                            }
                        }) {
                            Text(text = "Cerrar sesión")
                        }
                    }
                }
            }
        )
    }
}



