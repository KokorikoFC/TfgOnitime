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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = remember { SettingsViewModel(applicationContext) }
            val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()
            TfgOnitimeTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                val authViewModel = AuthViewModel()
                val languageViewModel = LanguageViewModel()
                val diaryViewModel = DiaryViewModel()
                val missionViewModel = MissionViewModel() // Primero declara missionViewModel
                val taskViewModel = TaskViewModel(missionViewModel) // Luego usa missionViewModel para instanciar taskViewModel
                val groupViewModel = GroupViewModel()
                val chatViewModel = ChatViewModel()
                val streakViewModel = StreakViewModel()
                val settingsViewModel: SettingsViewModel = remember { SettingsViewModel(applicationContext) }

                // Cargar el idioma guardado en las preferencias
                LanguageManager.loadLocale(this)
                languageViewModel.setLocale(languageViewModel.locale.value)

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
                    settingsViewModel = settingsViewModel,
                )
            }
        }
    }
}
