package com.example.tfgonitime.ui.screens.diary

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.painterResource
import com.example.tfgonitime.R
import com.example.tfgonitime.data.model.Mood
import com.example.tfgonitime.ui.theme.Green
import com.example.tfgonitime.viewmodel.DiaryViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MoodSelectionScreen(
    navHostController: NavHostController,
    selectedDate: LocalDate,
    diaryViewModel: DiaryViewModel,
) {
    val moodOptions = listOf(
        R.drawable.happy_face to "Bien",
        R.drawable.happy_face to "Bien Mal",
        R.drawable.happy_face to "Go go go",
        R.drawable.happy_face to "Go go go",
        R.drawable.happy_face to "Go go go"
    )

    val diaryEntry = remember { mutableStateOf("") }
    val moodType = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp) // Margen superior
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Cabecera con flecha de volver y fecha centrada
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
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
                text = "${selectedDate.dayOfMonth} - ${selectedDate.monthValue} - ${selectedDate.year}",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.size(24.dp)) // Espaciado para alinear
        }

        // Título
        Text(
            text = "¿Cómo te sientes hoy?",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black,
            modifier = Modifier
                .padding(bottom = 24.dp)
                .align(Alignment.CenterHorizontally)
        )

        // Opciones de estado de ánimo
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            moodOptions.forEachIndexed { index, (emojiResId, label) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { moodType.value = label }
                ) {
                    Image(
                        painter = painterResource(id = emojiResId),
                        contentDescription = label,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                color = if (moodType.value == label) Color(0xFFF5F5F5) else Color.Transparent
                            )
                            .padding(8.dp)
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // Campo para registrar el día
        TextField(
            value = diaryEntry.value,
            onValueChange = { diaryEntry.value = it },
            placeholder = { Text("Registra tu día") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(MaterialTheme.shapes.medium)
                .border(
                    1.dp,
                    Color.Gray,
                    shape = MaterialTheme.shapes.medium
                ), // Borde personalizado
            colors = TextFieldDefaults.colors (
                focusedContainerColor = Color.Transparent, // Sin fondo al enfocar
                unfocusedContainerColor = Color.Transparent, // Sin fondo al desenfocar
                focusedIndicatorColor = Color.Transparent, // Sin línea de indicador al enfocar
                unfocusedIndicatorColor = Color.Transparent, // Sin línea de indicador al desenfocar
                cursorColor = Color.Black, // Cursor negro
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón Guardar
        Button(
            onClick = {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    val mood = Mood(
                        id = userId,
                        moodDate = selectedDate?.toString() ?: "",
                        moodType = moodType.value,
                        diaryEntry = diaryEntry.value,
                        generatedLetter = null // Puedes generar esto más tarde
                    )
                    diaryViewModel.addMood(userId, mood)
                    navHostController.popBackStack()
                } else {
                    Log.e("SaveMood", "Error: Usuario no autenticado")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
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
}