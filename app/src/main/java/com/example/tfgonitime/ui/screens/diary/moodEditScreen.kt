package com.example.tfgonitime.ui.screens.diary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.wear.compose.material3.OutlinedButton
import com.example.tfgonitime.R
import com.example.tfgonitime.ui.components.MoodOptions
import com.example.tfgonitime.viewmodel.DiaryViewModel

@Composable
fun MoodEditScreen(
    navHostController: NavHostController,
    diaryViewModel: DiaryViewModel,
    moodDate: String,
) {
    val mood by diaryViewModel.selectedMood.collectAsState()
    var diaryEntry by remember { mutableStateOf("") }
    val selectedMood = remember { mutableStateOf("") }

    // Obtener el mood al iniciar la pantalla
    LaunchedEffect(moodDate) {
        diaryViewModel.getMoodById(moodDate)
    }

    // Sincronizar `selectedMood` y `diaryEntry` con los valores iniciales del `mood`
    LaunchedEffect(mood) {
        mood?.let {
            if (selectedMood.value.isEmpty()) selectedMood.value = it.moodType
            if (diaryEntry.isEmpty()) diaryEntry = it.diaryEntry
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Editar estado de ánimo",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth(),
        )

        Divider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        )

        mood?.let {
            Text(
                text = "ID: ${it.id}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            )

            Text(
                text = "Fecha: ${it.moodDate}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            )

            // seleccionar `MoodOptions`
            MoodOptions(selectedMood)

            // Editar la entrada del diario
            OutlinedTextField(
                value = diaryEntry,
                onValueChange = { diaryEntry = it },
                label = { Text(text = "Entrada del Diario") },
                modifier = Modifier.fillMaxWidth(),
            )

            it.generatedLetter?.let { letter ->
                Text(
                    text = "Carta Generada: $letter",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                )
            }
        }

        // Botones de acción
        Button(
            onClick = {
                mood?.let { updatedMood ->
                    mood?.let { updatedMood ->
                        val newMood = updatedMood.copy(
                            moodType = selectedMood.value,
                            diaryEntry = diaryEntry
                        )
                        diaryViewModel.updateMood(newMood)
                        navHostController.popBackStack()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Guardar cambios")
        }

        OutlinedButton(
            onClick = { navHostController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Cancelar")
        }
    }
}

