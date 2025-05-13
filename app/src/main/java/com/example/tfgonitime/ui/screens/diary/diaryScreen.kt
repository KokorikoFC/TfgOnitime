package com.example.tfgonitime.ui.screens.diary

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.data.model.Mood
import com.example.tfgonitime.ui.components.AnimatedMessage
import com.example.tfgonitime.ui.components.CustomBottomNavBar
import com.example.tfgonitime.ui.components.diaryComp.DaysGrid
import com.example.tfgonitime.ui.components.diaryComp.DeleteMood
import com.example.tfgonitime.ui.components.diaryComp.MonthSelector
import com.example.tfgonitime.ui.components.diaryComp.Mood
import com.example.tfgonitime.ui.components.diaryComp.MoodHandler
import com.example.tfgonitime.ui.components.diaryComp.ToggleTab
import com.example.tfgonitime.ui.theme.Green
import com.example.tfgonitime.viewmodel.DiaryViewModel
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.YearMonth


@Composable
fun DiaryScreen(navHostController: NavHostController, diaryViewModel: DiaryViewModel) {
    val currentMonth = remember { mutableStateOf(YearMonth.now()) }
    val selectedDay = remember { mutableStateOf<LocalDate?>(null) }
    val moodEmojis by diaryViewModel.moodEmojis.collectAsState()
    val record = remember { mutableStateOf(false) }
    val showMoodHandler = remember { mutableStateOf(false) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val monthlyMoods by diaryViewModel.moodsState.collectAsState()
    val isMoodRegisteredToday by diaryViewModel.isMoodRegisteredToday.collectAsState()
    val selectedMood by diaryViewModel.selectedMood.collectAsState()
    val moodToEdit = remember { mutableStateOf<Mood?>(null) }
    val showMoodDelete = remember { mutableStateOf(false) }
    val isLoading = remember { mutableStateOf(true) }  // State to track loading status

    LaunchedEffect(currentMonth.value) {
        if (userId.isNotEmpty()) {
            diaryViewModel.loadMoods(
                userId,
                currentMonth.value.year.toString(),
                currentMonth.value.monthValue.toString().padStart(2, '0')
            )
            isLoading.value = false  // Set isLoading to false once data is loaded
        }
    }

    LaunchedEffect(Unit) {
        if (userId.isNotEmpty()) {
            diaryViewModel.checkMoodRegisteredToday(userId)
        }
    }

    LaunchedEffect(navHostController.currentBackStackEntry) {
        diaryViewModel.clearSelectedMood() // Resetear selección
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.White,
            bottomBar = {
                if (!showMoodHandler.value) {
                    CustomBottomNavBar(navHostController)
                }
            },
            content = { paddingValues ->
                if (isLoading.value) {
                    // Show loading indicator if data is still loading
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(start = 20.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
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
                        ToggleTab(record = record, diaryViewModel = diaryViewModel)

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
                                        val firstDayOfWeek =
                                            (firstDayOfMonth.dayOfWeek.value + 5) % 7 + 1

                                        // Añadir cajas vacías hasta el primer día del mes
                                        for (i in 0 until firstDayOfWeek) {
                                            item { Box(modifier = Modifier.size(30.dp)) }
                                        }

                                        items(currentMonth.value.lengthOfMonth()) { day ->
                                            val date =
                                                currentMonth.value.atDay(day + 1) // Día correspondiente
                                            val emojiResId =
                                                moodEmojis[date] // Emoji asociado al día

                                            // Llamada al componente `DayItem`
                                            DaysGrid(
                                                date = date,
                                                isSelected = selectedDay.value == date,
                                                emojiResId = emojiResId,
                                                onDaySelected = { selectedDate ->
                                                    diaryViewModel.checkMoodRegistered(
                                                        userId,
                                                        selectedDate.toString()
                                                    ) { isRegistered ->
                                                        selectedDay.value = selectedDate
                                                        if (!isRegistered) {
                                                            navHostController.navigate("moodSelectionScreen/${selectedDate.toString()}")
                                                        }
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }

                                // Mostrar botón si no hay mood registrado para hoy
                                if (!isMoodRegisteredToday && selectedMood == null) {
                                    // Botón para registrar el ánimo del día
                                    item {
                                        Spacer(modifier = Modifier.height(12.dp))

                                        Button(
                                            onClick = {
                                                val today = LocalDate.now()
                                                navHostController.navigate("moodSelectionScreen/${today.toString()}")
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth(0.7f)
                                                .height(40.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Green),
                                            shape = RoundedCornerShape(8.dp) // Ajustar esquinas
                                        ) {
                                            Text(
                                                text = stringResource(R.string.toggle_tab_btn_register),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.White
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))
                                    }

                                    // Texto motivacional
                                    item {
                                        Text(
                                            text = stringResource(R.string.toggle_tab_text_register),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray,
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))
                                    }
                                }

                                // Mostrar mood seleccionado
                                selectedMood?.let { mood ->
                                    item {
                                        Mood(
                                            mood = mood,
                                            onMoreClick = { selectedMood ->
                                                showMoodHandler.value = true
                                                moodToEdit.value = selectedMood
                                            },
                                            navController = navHostController
                                        )
                                    }
                                }

                            } else {

                                items(monthlyMoods) { mood ->
                                    Spacer(modifier = Modifier.height(25.dp))
                                    Mood(
                                        mood = mood,
                                        onMoreClick = { selectedMood ->
                                            showMoodHandler.value = true
                                            moodToEdit.value = selectedMood
                                        },
                                        navController = navHostController
                                    )
                                }

                                // Mostrar mensaje si no hay moods registrados
                                if (monthlyMoods.isEmpty()) {
                                    item {
                                        Spacer(modifier = Modifier.height(25.dp))
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "No hay entradas en el diario para este mes. Presiona la fecha deseada en el calendario para escribir una entrada en el diario.",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.Gray,
                                                textAlign = TextAlign.Center
                                            )
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        )

        if (showMoodHandler.value && showMoodDelete.value == false) {
            MoodHandler(
                mood = moodToEdit.value!!,
                navHostController = navHostController,
                onEliminarClick = { showMoodDelete.value = true },
                onClose = { showMoodHandler.value = false }
            )
        }

        if (showMoodDelete.value) {
            DeleteMood(
                mood = moodToEdit.value!!,
                diaryViewModel = diaryViewModel,
                onClose = { showMoodDelete.value = false },
                onDelete = {
                    showMoodDelete.value = false
                    showMoodHandler.value = false // Close MoodHandler on delete
                }
            )
        }
    }
}