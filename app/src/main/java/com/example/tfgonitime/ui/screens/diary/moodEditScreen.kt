package com.example.tfgonitime.ui.screens.diary

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tfgonitime.ui.components.AnimatedMessage
import com.example.tfgonitime.ui.components.diaryComp.MoodOptions
import com.example.tfgonitime.ui.theme.Green
import com.example.tfgonitime.viewmodel.DiaryViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MoodEditScreen(
    navHostController: NavHostController,
    diaryViewModel: DiaryViewModel,
    moodDate: String,
) {
    val mood by diaryViewModel.selectedMood.collectAsState()
    var diaryEntry by remember { mutableStateOf("") }
    val selectedMood = remember { mutableStateOf("") }

    // Variables para manejar errores
    var errorMessage by remember { mutableStateOf("") }
    var isErrorVisible by remember { mutableStateOf(false) }

    // Obtener el mood al iniciar la pantalla
    LaunchedEffect(moodDate) {
        diaryViewModel.getMoodById(moodDate)
    }

    // Sincronizar `selectedMood` y `diaryEntry` con los valores iniciales del `mood`
    LaunchedEffect(mood) {
        mood?.let {
            if (selectedMood.value.isEmpty()) selectedMood.value = it.moodType
            println ("Mood Seleccionado: " + selectedMood.value)
            if (diaryEntry.isEmpty()) diaryEntry = it.diaryEntry
        }
    }

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
                .padding(top = 50.dp)
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

            mood?.let {
                Text(
                    text = formatDateForDisplay(it.moodDate),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.size(24.dp)) // Espaciado para alinear
        }

        Spacer(modifier = Modifier.height(24.dp)) // Espaciado para alinear

        // Título
        Text(
            text = "Editar estado de ánimo",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black,
            modifier = Modifier
                .padding(bottom = 24.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(20.dp)) // Espaciado

        // Opciones de estado de ánimo
        MoodOptions(selectedMood)

        Spacer(modifier = Modifier.height(30.dp)) // Espaciado

        // Campo para registrar el día
        OutlinedTextField(
            value = diaryEntry,
            onValueChange = { diaryEntry = it },
            placeholder = { Text("Edita tu entrada del día") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(MaterialTheme.shapes.medium)
                .border(
                    1.dp,
                    Color.Gray,
                    shape = MaterialTheme.shapes.medium
                ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent, // Sin fondo al enfocar
                unfocusedContainerColor = Color.Transparent, // Sin fondo al desenfocar
                focusedIndicatorColor = Color.Transparent, // Sin línea de indicador al enfocar
                unfocusedIndicatorColor = Color.Transparent, // Sin línea de indicador al desenfocar
                cursorColor = Color.Black, // Cursor negro
            ),
        )

        Spacer(modifier = Modifier.height(84.dp)) // Espaciado para alinear

        // Botón Guardar
        Button(
            onClick = {
                mood?.let { updatedMood ->
                    val newMood = updatedMood.copy(
                        moodType = selectedMood.value,
                        diaryEntry = diaryEntry
                    )
                    diaryViewModel.updateMood(
                        newMood,
                        onSuccess = {
                            navHostController.popBackStack()
                        },
                        onError = { error ->
                            errorMessage = error // Asigna el mensaje de error
                            isErrorVisible = true // Muestra el mensaje animado
                        }
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Green),
            shape = RoundedCornerShape(8.dp) // Ajustar esquinas
        ) {
            Text(
                text = "Guardar cambios",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(16.dp)) // Espaciado para alinear
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
            onDismiss = { isErrorVisible = false },
            isWhite = false
        )
    }
}

fun formatDateForDisplay(dateString: String): String {
    // Definir el formato de fecha de entrada y salida
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // Parsear la fecha y luego formatearla
    val date = LocalDate.parse(dateString, inputFormatter)
    return date.format(outputFormatter)
}