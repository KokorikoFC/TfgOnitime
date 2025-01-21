package com.example.tfgonitime.ui.screens.diary

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tfgonitime.ui.components.CustomBottomNavBar
import com.example.tfgonitime.ui.components.DaysGrid
import com.example.tfgonitime.ui.components.MonthSelector
import com.example.tfgonitime.ui.components.Mood
import com.example.tfgonitime.ui.components.ToggleTab
import com.example.tfgonitime.ui.theme.Green
import com.example.tfgonitime.viewmodel.DiaryViewModel
import com.example.tfgonitime.viewmodel.MoodViewModel
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun DiaryScreen(navHostController: NavHostController, diaryViewModel: DiaryViewModel) {

    val currentMonth = remember { mutableStateOf(YearMonth.now()) }
    val selectedDay = remember { mutableStateOf<LocalDate?>(null) }
    val moodEmojis by diaryViewModel.moodEmojis.collectAsState()
    val record = remember { mutableStateOf(false) }

    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val monthlyMoods by diaryViewModel.monthlyMoods.collectAsState()

    LaunchedEffect(currentMonth.value) {
        if (userId != null) {
            diaryViewModel.loadMoods(
                userId,
                currentMonth.value.year.toString(),
                currentMonth.value.monthValue.toString().padStart(2, '0')
            )
            diaryViewModel.loadMonthlyMoods(
                userId,
                currentMonth.value.year.toString(),
                currentMonth.value.monthValue.toString().padStart(2, '0')
            )
        }
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = { CustomBottomNavBar(navHostController) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(
                        start = 16.dp,
                        top = 16.dp,
                        end = 16.dp // Padding solo en los lados (izquierda, derecha y arriba)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Componente para seleccionar el mes
                MonthSelector(
                    userId = userId,
                    currentMonth = currentMonth,
                    diaryViewModel = diaryViewModel,
                    onMonthChange = { newMonth ->
                        selectedDay.value = null
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de calendario o historial
                ToggleTab(record = record)

                Spacer(modifier = Modifier.height(12.dp))

                // Sección desplazable (con scroll)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (record.value == false) {
                        // Cuadrícula de días del mes seleccionado
                        item {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(7),
                                modifier = Modifier
                                    .padding(top = 18.dp)
                                    .fillMaxWidth()
                                    .height(400.dp) // Ajustar altura para scroll interno
                            ) {

                                // Obtener el primer día del mes
                                val firstDayOfMonth = currentMonth.value.atDay(1)

                                // Calcular el índice del primer día de la semana correctamente (1 a 7, donde 1 es lunes y 7 es domingo)
                                val firstDayOfWeek = (firstDayOfMonth.dayOfWeek.value + 5) % 7 + 1

                                // Añadir cajas vacías hasta el primer día del mes
                                for (i in 0 until firstDayOfWeek) {
                                    item { Box(modifier = Modifier.size(30.dp)) }
                                }

                                items(currentMonth.value.lengthOfMonth()) { day ->
                                    val date =
                                        currentMonth.value.atDay(day + 1) // Día correspondiente
                                    val emojiResId = moodEmojis[date] // Emoji asociado al día

                                    // Llamada al componente `DayItem`
                                    DaysGrid(
                                        date = date,
                                        isSelected = selectedDay.value == date,
                                        emojiResId = emojiResId,
                                        onDaySelected = { selectedDate ->
                                            selectedDay.value = selectedDate
                                            navHostController.navigate("moodSelectionScreen/${selectedDate.toString()}")
                                        }
                                    )
                                }
                            }
                        }
                        // Botón para registrar el ánimo del día
                        item {
                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    val today = LocalDate.now()
                                    navHostController.navigate("moodSelectionScreen/${today.toString()}")
                                },
                                modifier = Modifier
                                    .fillMaxWidth(0.65f)
                                    .height(40.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Green),
                                shape = RoundedCornerShape(8.dp) // Ajustar esquinas
                            ) {
                                Text(
                                    text = "Registrar el ánimo de hoy",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Texto motivacional
                        item {
                            Text(
                                text = "¡Escribe en tu diario y recibe apoyo diario!",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                    } else {

                        items(monthlyMoods) { mood ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Mood(
                                moodDate = mood.moodDate,
                                moodType = mood.moodType,
                                diaryEntry = mood.diaryEntry
                            )
                        }
                    }

                }
            }
        }
    )
}