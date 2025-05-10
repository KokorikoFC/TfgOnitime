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
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import com.example.tfgonitime.presentation.viewmodel.PetsViewModel
import android.app.Application

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        LanguageManager.loadLocale(this)

        setContent {
            val applicationContext = application

            val settingsViewModel: SettingsViewModel = remember { SettingsViewModel(applicationContext) }
            val authViewModel = remember { AuthViewModel() }
            val languageViewModel = remember { LanguageViewModel() }
            val diaryViewModel = remember { DiaryViewModel() }
            val groupViewModel = remember { GroupViewModel() }
            val chatViewModel = remember { ChatViewModel() }
            val streakViewModel = remember { StreakViewModel() }
            val furnitureViewModel = remember { FurnitureViewModel() }
            val petsViewModel = remember { PetsViewModel() }
            val missionViewModel = remember { MissionViewModel() }
            val taskViewModel = remember {
                TaskViewModel(applicationContext as Application, missionViewModel)
            }


            val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()
            TfgOnitimeTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()


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