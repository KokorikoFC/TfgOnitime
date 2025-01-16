package com.example.tfgonitime.ui.screens.diary

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.example.tfgonitime.ui.components.ToggleTab
import com.example.tfgonitime.ui.theme.Green
import com.example.tfgonitime.viewmodel.DiaryViewModel
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun DiaryScreen(navHostController: NavHostController, diaryViewModel: DiaryViewModel){

    val currentMonth = remember { mutableStateOf(YearMonth.now()) }
    val selectedDay = remember { mutableStateOf<LocalDate?>(null) }
    val moodEmojis = diaryViewModel.moodEmojis.value // Obtener los emojis registrados

    Scaffold(
        containerColor = Color.White,
        bottomBar = { CustomBottomNavBar(navHostController) },
        content = { paddingValues ->
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp), // Padding adicional de 16dp en toda la pantalla
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Seleccionable mes
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        currentMonth.value = currentMonth.value.minusMonths(1)
                        selectedDay.value = null
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Mes anterior",
                            tint = Color.Black
                        )
                    }

                    Text(
                        text = "${currentMonth.value.year}.${currentMonth.value.monthValue.toString().padStart(2, '0')}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black
                    )

                    IconButton(onClick = {
                        currentMonth.value = currentMonth.value.plusMonths(1)
                        selectedDay.value = null
                    }) {
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = "Mes siguiente",
                            tint = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de calendario o historial
                ToggleTab()

                Spacer(modifier = Modifier.height(30.dp))

                // Lista de días del mes seleccionado
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.weight(1f)
                ) {
                    items(currentMonth.value.lengthOfMonth()) { day ->

                        val date = currentMonth.value.atDay(day + 1)
                        val emojiResId = moodEmojis[date] // Color predeterminado si no hay estado de ánimo

                        Box(
                            modifier = Modifier
                                .padding(8.dp) // Espaciado entre los círculos
                                .clickable { selectedDay.value = date },
                            contentAlignment = Alignment.Center
                        ) {
                            // Column para apilar el círculo y el número
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxSize() // Aseguramos que ocupe todo el espacio del Box
                            ) {
                                // Emoji encima del número
                                if (emojiResId != null) {
                                    Image(
                                        painter = painterResource(id = emojiResId),
                                        contentDescription = "Estado de ánimo",
                                        modifier = Modifier
                                            .size(40.dp) // Tamaño del emoji
                                            .clickable {
                                                selectedDay.value = date
                                                navHostController.navigate("moodSelectionScreen/${date.toString()}")
                                            }
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFEFEFEF)) // Fondo gris si no hay emoji
                                            .clickable {
                                                selectedDay.value = date
                                                navHostController.navigate("moodSelectionScreen/${date.toString()}")
                                            }
                                    )
                                }


                                // Número del día debajo del círculo
                                Box(
                                    modifier = Modifier.padding(top = 6.dp) // Espacio entre círculo y número
                                ) {
                                    Text(
                                        text = "${day + 1}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp), // Ajustar el tamaño de fuente
                                        color = Color.Black,
                                    )
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        selectedDay.value?.let { date ->
                            navHostController.navigate("moodSelectionScreen/${date.toString()}")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Green),
                    shape = RoundedCornerShape(8.dp) // Cambia este valor para ajustar las esquinas
                ) {
                    Text(
                        text = "Registrar el ánimo de hoy",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "¡Escribe en tu diario y recibe apoyo para tu día!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Moods del mes seleccionado

            }
        }
    )
}