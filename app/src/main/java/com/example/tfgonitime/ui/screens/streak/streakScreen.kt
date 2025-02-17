package com.example.tfgonitime.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.tfgonitime.viewmodel.StreakViewModel
import com.google.firebase.auth.FirebaseAuth
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun StreakScreen(navHostController: NavHostController, streakViewModel: StreakViewModel) {

    val streakState by streakViewModel.streakState.collectAsState()
    val loadingState by streakViewModel.loadingState.collectAsState()

    val daysOfWeek = remember { DayOfWeek.values().toList() }
    val streakDaysState = remember {
        mutableStateMapOf<DayOfWeek, Boolean>().apply {
            daysOfWeek.forEach { dayOfWeek ->
                put(dayOfWeek, false)
            }
        }
    }

    // Obtener userId desde FirebaseAuth
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid // Obtener el UID del usuario actual (puede ser nulo si no hay usuario logueado)

    LaunchedEffect(userId) { // Ahora LaunchedEffect depende de userId
        if (userId != null) { // Verificar que userId no sea nulo
            streakViewModel.loadStreak(userId)
            // En una app real, cargar StreakDay aquí también
        } else {
            // Manejar el caso en que no hay usuario logueado (opcional, depende de tu app)
            println("No user logged in")
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Racha de 7 días")
        Spacer(modifier = Modifier.height(16.dp))

        if (loadingState) {
            CircularProgressIndicator() // Mostrar un indicador de carga mientras se carga la racha
        } else {
            // Mostrar los círculos de los días de la semana
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(daysOfWeek) { dayOfWeek ->
                    DayCircle(
                        dayOfWeek = dayOfWeek,
                        isCompleted = streakDaysState[dayOfWeek] ?: false // Usar el estado local para mostrar completado o no
                        // En una app real, isCompleted debería venir del StreakDay del backend
                    ) {
                        // Acción al hacer clic en un círculo (opcional, si quieres que los círculos sean interactivos)
                        // Por ejemplo, podrías llamar a streakViewModel.markDayCompleted(...) aquí si permites marcar días manualmente
                        println("Clicked on ${dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())}")
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Mostrar la racha actual
            Text(text = "Racha actual: ${streakState?.currentStreak ?: 0} periodos de 7 días")
            Spacer(modifier = Modifier.height(24.dp))

            // Botón para marcar el día como completado (simula el inicio de sesión diario)
            Button(onClick = {
                userId?.let { // Usar let para ejecutar solo si userId no es nulo
                    streakViewModel.updateStreakOnLogin(it) // Pasar userId obtenido de FirebaseAuth
                    val currentDayOfWeek = DayOfWeek.from(java.time.LocalDate.now())
                    streakDaysState[currentDayOfWeek] = true
                } ?: run {
                    // Manejar el caso en que userId es nulo (opcional)
                    println("No user ID available")
                }
            }) {
                Text("¡Abrir la App Hoy!")
            }
        }
    }
}

@Composable
fun DayCircle(dayOfWeek: DayOfWeek, isCompleted: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onClick, modifier = Modifier.size(40.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) Color.Green else Color.LightGray) // Color según si está completado
            )
        }
        Text(
            text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()), // Nombre corto del día (Lun, Mar, etc.)
        )
    }
}

