package com.example.tfgonitime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.rememberNavController
import com.example.tfgonitime.data.repository.LanguageManager
import com.example.tfgonitime.ui.navigation.NavigationWrapper
import com.example.tfgonitime.ui.theme.TfgOnitimeTheme
import com.example.tfgonitime.viewmodel.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext // Puede ser necesario si tus ViewModels necesitan Context
import androidx.lifecycle.viewmodel.compose.viewModel // Útil para instanciar ViewModels con Factory
import com.example.tfgonitime.presentation.viewmodel.PetsViewModel
import android.app.Application // Importa Application

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // **RECOMENDADO:** Cargar el idioma guardado AQUÍ, antes de setContent
        // Esto se hace al inicio de la Activity
        LanguageManager.loadLocale(this)
        // languageViewModel aún no está listo aquí para setLocale en él

        setContent {
            // Obtener el contexto de la aplicación. Es mejor usar applicationContext de la Activity.
            val applicationContext = application // Propiedad disponible en ComponentActivity

            // **CORRECCIÓN Y RECOMENDACIÓN:** Instanciar ViewModels usando remember
            // para asegurar una única instancia asociada al ciclo de vida de este composable raíz.

            // Instanciar ViewModels que no necesitan otros ViewModels en su constructor primero
            val settingsViewModel: SettingsViewModel = remember { SettingsViewModel(applicationContext) }
            val authViewModel = remember { AuthViewModel() } // Revisa si necesita algún argumento
            // LanguageViewModel probablemente necesite applicationContext si maneja persistencia de idioma
            val languageViewModel = remember { LanguageViewModel() } // Revisa constructor
            val diaryViewModel = remember { DiaryViewModel() } // Revisa constructor
            val groupViewModel = remember { GroupViewModel() } // Revisa constructor
            val chatViewModel = remember { ChatViewModel() } // Revisa constructor
            val streakViewModel = remember { StreakViewModel() } // Revisa constructor
            val furnitureViewModel = remember { FurnitureViewModel() } // Revisa constructor
            val petsViewModel = remember { PetsViewModel() } // Revisa constructor

            // Instanciar MissionViewModel (asumiendo su constructor)
            val missionViewModel = remember { MissionViewModel() } // Revisa si necesita algún argumento

            // **CORRECCIÓN PRINCIPAL:** Instanciar taskViewModel pasando el Application context primero
            val taskViewModel = remember {
                TaskViewModel(applicationContext as Application, missionViewModel) // <-- CORREGIDO
            }


            val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()
            TfgOnitimeTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()

                // Si LanguageViewModel maneja la carga/guardado del locale, quizás necesite applicationContext
                // y podrías querer llamar a su método para cargar el locale aquí con un LaunchedEffect
                // después de instanciarlo.
                /*
                LaunchedEffect(languageViewModel) {
                     // Si LanguageViewModel tiene un método como loadLocaleFromPrefs(context)
                     // languageViewModel.loadLocaleFromPrefs(applicationContext)
                     // O si setLocale(locale) ya lo hace internamente al crearse...
                }
                 */


                NavigationWrapper(
                    navHostController = navController,
                    authViewModel = authViewModel,
                    languageViewModel = languageViewModel,
                    diaryViewModel = diaryViewModel,
                    taskViewModel = taskViewModel, // Pasa la instancia correctamente creada
                    groupViewModel = groupViewModel,
                    chatViewModel = chatViewModel,
                    streakViewModel = streakViewModel,
                    missionViewModel = missionViewModel, // Pasa la instancia correctamente creada
                    furnitureViewModel = furnitureViewModel,
                    settingsViewModel = settingsViewModel,
                    petsViewModel = petsViewModel
                )
            }
        }
    }
}