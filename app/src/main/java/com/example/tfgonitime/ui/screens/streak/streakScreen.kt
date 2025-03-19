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
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.draw.clip
import androidx.navigation.NavHostController

@Composable
fun StreakScreen(navHostController: NavHostController, streakViewModel: StreakViewModel) {
    val currentStreak by streakViewModel.currentStreak.collectAsState()
    val longestStreak by streakViewModel.longestStreak.collectAsState()
    val loadingState by streakViewModel.loadingState.collectAsState()
    val updateSuccess by streakViewModel.updateSuccess.collectAsState() // Nuevo estado para verificar éxito
    val updateError by streakViewModel.updateError.collectAsState() // Nuevo estado para errores

    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    LaunchedEffect(userId) {
        userId?.let {
            streakViewModel.loadStreak(it)
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
            CircularProgressIndicator()
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(7) { dayIndex ->
                    val isCompleted = dayIndex < currentStreak
                    DayCircle(isCompleted = isCompleted)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Racha actual: $currentStreak días")
            Text(text = "Racha más larga: $longestStreak días")
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    userId?.let {
                        streakViewModel.onOpenAppTodayClicked(it)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("¡Abrir la App Hoy!")
            }
        }
    }

    // Mostrar error si hay problemas con la actualización
    if (updateError != null) {
        LaunchedEffect(updateError) {
            Log.e("StreakScreen", "Error al actualizar racha")
        }
    }

    // Navegar solo si la actualización fue exitosa
    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            navHostController.navigate("homeScreen") {
                popUpTo("streakScreen") { inclusive = true }
            }
            streakViewModel.clearUpdateState() // Resetear estado después de la navegación
        }
    }
}

@Composable
fun DayCircle(
    isCompleted: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isCompleted) Color.Green else Color.LightGray)
        )
    }
}