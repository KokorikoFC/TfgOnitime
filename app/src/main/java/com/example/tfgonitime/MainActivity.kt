package com.example.tfgonitime

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.example.tfgonitime.data.repository.LanguageManager
import com.example.tfgonitime.ui.theme.TfgOnitimeTheme
import com.example.tfgonitime.viewmodel.AuthViewModel
import com.example.tfgonitime.viewmodel.SettingsViewModel
import com.example.tfgonitime.presentation.viewmodel.PetsViewModel
import com.example.tfgonitime.ui.navigation.NavigationWrapper
import com.example.tfgonitime.viewmodel.*


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Cargar idioma antes de establecer el contenido
        LanguageManager.loadLocale(this)

        setContent {
            val applicationContext = application as Application

            val settingsViewModel: SettingsViewModel = remember { SettingsViewModel(applicationContext) }
            val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()

            val authViewModel = remember { AuthViewModel() }
            val userId by authViewModel.userId.collectAsState() // Obtenemos el userId desde el ViewModel

            TfgOnitimeTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()

                val languageViewModel = remember { LanguageViewModel() }
                val diaryViewModel = remember { DiaryViewModel() }
                val missionViewModel = remember { MissionViewModel() }
                val taskViewModel = remember { TaskViewModel(applicationContext, missionViewModel) }
                val groupViewModel = remember { GroupViewModel() }
                val chatViewModel = remember { ChatViewModel() }
                val streakViewModel = remember { StreakViewModel() }
                val furnitureViewModel = remember { FurnitureViewModel() }
                val petsViewModel = remember { PetsViewModel() }

                // Aquí solo gestionamos el userId y programamos el worker si es necesario
                if (!userId.isNullOrEmpty()) {
                    // Si el userId no es nulo o vacío, programamos el Worker
                    taskViewModel.scheduleReminderResetWorker(userId!!)
                }

                NavigationWrapper(
                    navHostController = navController,
                    authViewModel = authViewModel,
                    languageViewModel = languageViewModel,
                    diaryViewModel = diaryViewModel,
                    taskViewModel = taskViewModel,
                    groupViewModel = groupViewModel,
                    chatViewModel = chatViewModel,
                    streakViewModel = streakViewModel,
                    missionViewModel = missionViewModel,
                    furnitureViewModel = furnitureViewModel,
                    settingsViewModel = settingsViewModel,
                    petsViewModel = petsViewModel
                )
            }
        }
    }
}
