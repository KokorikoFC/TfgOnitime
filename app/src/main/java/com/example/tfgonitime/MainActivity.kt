package com.example.tfgonitime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.tfgonitime.data.repository.LanguageManager
import com.example.tfgonitime.ui.navigation.NavigationWrapper
import com.example.tfgonitime.ui.theme.TfgOnitimeTheme
import com.example.tfgonitime.viewmodel.AuthViewModel
import com.example.tfgonitime.viewmodel.DiaryViewModel
import com.example.tfgonitime.viewmodel.LanguageViewModel

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

                // Cargar el idioma guardado en las preferencias
                LanguageManager.loadLocale(this)
                languageViewModel.setLocale(languageViewModel.locale.value)

                NavigationWrapper(navHostController = navController, authViewModel = authViewModel, languageViewModel = languageViewModel, diaryViewModel = diaryViewModel)

                /*
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                }*/
            }
        }
    }
}

