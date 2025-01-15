package com.example.tfgonitime.ui.screens.setting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.data.repository.LanguageManager
import com.example.tfgonitime.ui.components.CustomBottomNavBar
import com.example.tfgonitime.ui.components.CustomRadioButton
import com.example.tfgonitime.viewmodel.AuthViewModel
import com.example.tfgonitime.viewmodel.LanguageViewModel
import java.util.Locale

@Composable
fun SettingScreen(
    navHostController: NavHostController,
    authViewModel: AuthViewModel,
    languageViewModel: LanguageViewModel,
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

    // Actualizar el idioma seleccionado cuando se carga el idioma desde LanguageViewModel
    LaunchedEffect(locale) {
        selectedLanguage = languages.find { it.second.language == locale.language }?.first
            ?: languages[0].first
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = { CustomBottomNavBar(navHostController) },
        content = { paddingValues ->


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Idioma",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Text(text = stringResource(R.string.forgot_password))

                Spacer(modifier = Modifier.height(16.dp))

                languages.forEach { (languageName, localeOption) ->
                    LanguageOption(
                        text = languageName,
                        isSelected = languageName == selectedLanguage,
                        onClick = {
                            selectedLanguage = languageName
                            LanguageManager.setLocale(context, localeOption)
                            languageViewModel.setLocale(localeOption)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    authViewModel.logout {
                        navHostController.navigate("loginScreen") // Navega a la pantalla de login
                    }
                }) {
                    Text(text = "Cerrar sesión")
                }

            }
        }
    )
}

@Composable
fun LanguageOption(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = TextStyle(fontSize = 16.sp, color = Color.Black),
            modifier = Modifier.weight(1f)
        )
        CustomRadioButton(isSelected = isSelected, onClick = onClick)
    }
}