package com.example.tfgonitime.ui.screens.diary

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.example.tfgonitime.R
import com.example.tfgonitime.data.model.Mood
import com.example.tfgonitime.ui.components.AnimatedMessage
import com.example.tfgonitime.ui.components.diaryComp.MoodOptions
import com.example.tfgonitime.ui.theme.Green
import com.example.tfgonitime.viewmodel.DiaryViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MoodSelectionScreen(
    navHostController: NavHostController,
    selectedDate: LocalDate,
    diaryViewModel: DiaryViewModel,
) {

    val diaryEntry = remember { mutableStateOf("") }
    val selectedMood = remember { mutableStateOf("") }

    // Variables para manejar errores
    var errorMessage by remember { mutableStateOf("") }
    var isErrorVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Cabecera con flecha de volver y fecha centrada
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(top = 50.dp) // Margen superior
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            IconButton(
                onClick = { navHostController.popBackStack() },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver atrás",
                    tint = Color.Black
                )
            }

            Text(
                text = "${selectedDate.dayOfMonth}/${selectedDate.monthValue}/${selectedDate.year}",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black
            )

            Spacer(modifier = Modifier.size(24.dp)) // Espaciado para alinear
        }

        Spacer(modifier = Modifier.height(24.dp)) // Espaciado para alinear

        // Título
        Text(
            text = stringResource(R.string.mood_prompt),
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black,
            modifier = Modifier
                .padding(bottom = 24.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(20.dp)) // Espaciado para alinear

        // Opciones de estado de ánimo
        MoodOptions(selectedMood)

        Spacer(modifier = Modifier.height(30.dp)) // Espaciado para alinear

        // Campo para registrar el día
        TextField(
            value = diaryEntry.value,
            onValueChange = { diaryEntry.value = it },
            placeholder = { Text(stringResource(R.string.mood_diary_entry)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(MaterialTheme.shapes.medium)
                .border(
                    1.dp,
                    Color.Gray,
                    shape = MaterialTheme.shapes.medium
                ), // Borde personalizado
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent, // Sin fondo al enfocar
                unfocusedContainerColor = Color.Transparent, // Sin fondo al desenfocar
                focusedIndicatorColor = Color.Transparent, // Sin línea de indicador al enfocar
                unfocusedIndicatorColor = Color.Transparent, // Sin línea de indicador al desenfocar
                cursorColor = Color.Black, // Cursor negro
            )
        )

        Spacer(modifier = Modifier.height(84.dp)) // Espaciado para alinear

        // Botón Guardar
        Button(
            onClick = {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    val mood = Mood(
                        id = userId,
                        moodDate = selectedDate?.toString() ?: "",
                        moodType = selectedMood.value,
                        diaryEntry = diaryEntry.value,
                        generatedLetter = null // Puedes generar esto más tarde
                    )
                    diaryViewModel.addMood(
                        userId,
                        mood,
                        onSuccess = {
                            navHostController.popBackStack()
                        },
                        onError = { error ->
                            errorMessage = error // Asigna el mensaje de error
                            isErrorVisible = true // Muestra el mensaje animado
                        }
                    )
                } else {
                    Log.e("SaveMood", "Error: Usuario no autenticado")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Green),
            shape = RoundedCornerShape(8.dp) // Ajustar esquinas
        ) {
            Text(
                text = "Guardar",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }

    }
    // Caja para el error
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedMessage(
            message = errorMessage,
            isVisible = isErrorVisible,
            onDismiss = { isErrorVisible = false }
        )
    }

}