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
import com.example.tfgonitime.worker.ReminderResetWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
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
                    scheduleReminderResetWorker(applicationContext, userId!!)
                }

                // NavigationWrapper se mantiene en su lugar
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

    // Función para programar el ReminderResetWorker
    private fun scheduleReminderResetWorker(context: Application, userId: String) {
        // Crea una instancia del Worker con los parámetros necesarios
        val reminderResetWorkerRequest: WorkRequest =
            OneTimeWorkRequestBuilder<ReminderResetWorker>()
                .setInputData(
                    workDataOf("USER_ID" to userId)
                )
                .build()

        // Enviar el Worker a WorkManager
        WorkManager.getInstance(context).enqueue(reminderResetWorkerRequest)
    }
}
