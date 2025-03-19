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
                val taskViewModel = TaskViewModel()
                val groupViewModel = GroupViewModel()
                val chatViewModel = ChatViewModel()
                val streakViewModel = StreakViewModel()


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
                    streakViewModel = streakViewModel
                )

                /*
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                }*/
            }
        }
    }
}

