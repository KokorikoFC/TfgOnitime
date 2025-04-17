package com.example.tfgonitime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.tfgonitime.data.repository.LanguageManager
import com.example.tfgonitime.ui.navigation.NavigationWrapper
import com.example.tfgonitime.ui.theme.TfgOnitimeTheme
import com.example.tfgonitime.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TfgOnitimeTheme {
                val navController = rememberNavController()
                val authViewModel = AuthViewModel()
                val languageViewModel = LanguageViewModel()
                val diaryViewModel = DiaryViewModel()
                val missionViewModel = MissionViewModel() // Primero declara missionViewModel
                val taskViewModel = TaskViewModel(missionViewModel) // Luego usa missionViewModel para instanciar taskViewModel
                val groupViewModel = GroupViewModel()
                val chatViewModel = ChatViewModel()
                val streakViewModel = StreakViewModel()
                val furnitureViewModel = FurnitureViewModel()

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
                    furnitureViewModel = furnitureViewModel
                )
            }
        }
    }
}
