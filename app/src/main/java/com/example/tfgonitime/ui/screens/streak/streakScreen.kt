package com.example.tfgonitime.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tfgonitime.viewmodel.StreakViewModel
import com.example.tfgonitime.data.model.StreakDay
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.clip


@Composable
fun StreakScreen(streakViewModel: StreakViewModel) {
    val currentStreak by streakViewModel.currentStreak.collectAsState()
    val longestStreak by streakViewModel.longestStreak.collectAsState()
    val loadingState by streakViewModel.loadingState.collectAsState()

    // Obtener userId desde FirebaseAuth
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid // Obtener el UID del usuario actual

    // Cargar la racha si el usuario está logueado
    LaunchedEffect(userId) {
        userId?.let {
            streakViewModel.loadStreak(it)  // Cargar la racha al inicio
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Racha de 7 días",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (loadingState) {
            CircularProgressIndicator() // Mostrar un indicador de carga
        } else {
            // Mostrar los círculos de los días secuenciales
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Crear una lista de días basada en currentStreak
                items(currentStreak) { day ->
                    val streakDay = StreakDay(completed = true) // Obtén el estado de los días desde Firestore
                    DayCircle(
                        isCompleted = streakDay.completed,
                        onClick = {
                            // Incrementar el día de la racha y actualizar
                            streakViewModel.updateStreak(userId ?: "", day)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Mostrar la racha actual y la más larga
            Text(text = "Racha actual: $currentStreak días")
            Text(text = "Racha más larga: $longestStreak días")
            Spacer(modifier = Modifier.height(24.dp))

            // Botón para marcar el día como completado
            Button(
                onClick = {
                    userId?.let {
                        streakViewModel.updateStreak(it, currentStreak+1)  // Pasa el valor actual de la racha
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("¡Abrir la App Hoy!")
            }
        }
    }
}

@Composable
fun DayCircle(
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(40.dp)
                .background(if (isCompleted) Color.Green else Color.LightGray)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(if (isCompleted) Color.Green else Color.LightGray)
                    .clip(CircleShape)
            )
        }
        Text("Día")
    }
}
