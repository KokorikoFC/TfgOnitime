package com.example.tfgonitime.ui.components.taskComp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tfgonitime.ui.theme.Brown
import com.example.tfgonitime.ui.theme.White


@Composable
fun ReminderTimePicker(
    selectedTime: String?,
    onTimeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Parsear la hora inicial si se proporciona
    val initialTimeParts = selectedTime?.split(":")
    // Convertir la hora inicial (en formato HH:mm 24h) a formato 12h y AM/PM para el picker
    val initialHour24 = initialTimeParts?.getOrNull(0)?.toIntOrNull() ?: 0
    val initialMinute = initialTimeParts?.getOrNull(1)?.toIntOrNull() ?: 0

    val initialHour12 = if (initialHour24 == 0 || initialHour24 == 12) 12 else initialHour24 % 12
    val initialAmPm = if (initialHour24 >= 12) "PM" else "AM"


    // Estado para manejar la visibilidad de los dropdowns
    var expandedHour by remember { mutableStateOf(false) }
    var expandedMinute by remember { mutableStateOf(false) }
    var expandedAmPm by remember { mutableStateOf(false) }

    val hoursList = (1..12).toList()
    val minutesList = (0..59).toList()
    val amPmList = listOf("AM", "PM")

    var selectedHour12 by remember { mutableStateOf(initialHour12) }
    var selectedMinute by remember { mutableStateOf(initialMinute) }
    var selectedAmPm by remember { mutableStateOf(initialAmPm) }



    LaunchedEffect(selectedHour12, selectedMinute, selectedAmPm) {
        // Convertir de formato 12h + AM/PM a formato 24h (HH:mm)
        val hour24 = when {
            selectedAmPm == "PM" && selectedHour12 != 12 -> selectedHour12 + 12
            selectedAmPm == "AM" && selectedHour12 == 12 -> 0 // 12 AM (medianoche) es 00 en 24h
            else -> selectedHour12 // 1-11 AM, 12 PM (mediodía) es 12 en 24h
        }
        // Formatear como String "HH:mm" (24 horas)
        val formattedTimeString = String.format("%02d:%02d", hour24, selectedMinute)
        onTimeSelected(formattedTimeString) // Llama al callback con el String formateado

    }

    LaunchedEffect(selectedTime) {
        selectedTime?.let {
            val parts = it.split(":")
            val h24 = parts.getOrNull(0)?.toIntOrNull() ?: 0
            val m = parts.getOrNull(1)?.toIntOrNull() ?: 0

            // Convertir la hora 24h parseada a formato 12h + AM/PM para el estado interno del picker
            selectedHour12 = if (h24 == 0 || h24 == 12) 12 else h24 % 12
            selectedAmPm = if (h24 >= 12) "PM" else "AM"
            selectedMinute = m
        }
    }


    // Mostrar los botones de hora, minuto y AM/PM
    Column(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                contentAlignment = Alignment.Center
            ) {
                TextButton(
                    onClick = { expandedHour = !expandedHour },
                ) {
                    Text(
                        text = String.format("%02d", selectedHour12),
                        color = Brown,
                        fontSize = 16.sp
                    )
                }
                DropdownMenu(
                    expanded = expandedHour,
                    onDismissRequest = { expandedHour = false },
                    modifier = Modifier
                        .heightIn(max = 200.dp)
                        .background(Brown)
                ) {
                    hoursList.forEach { hour ->
                        DropdownMenuItem(
                            onClick = {
                                selectedHour12 = hour
                                expandedHour = false
                            },
                            text = {
                                Text(
                                    text = hour.toString().padStart(2, '0'),
                                    color = White,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                )
                            }
                        )
                    }
                }
            }

            // Dropdown para los minutos
            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = { expandedMinute = !expandedMinute }) {
                    Text(
                        text = String.format("%02d", selectedMinute), color = Brown,
                        fontSize = 16.sp
                    )
                }
                DropdownMenu(
                    expanded = expandedMinute,
                    onDismissRequest = { expandedMinute = false },
                    modifier = Modifier
                        .heightIn(max = 200.dp)
                        .background(Brown)
                ) {
                    minutesList.forEach { minute ->
                        DropdownMenuItem(
                            onClick = {
                                selectedMinute = minute
                                expandedMinute = false
                            },
                            text = {
                                Text(
                                    text = String.format("%02d", minute), color = White,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                )

                            }
                        )
                    }
                }
            }

            // Dropdown para AM/PM
            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = { expandedAmPm = !expandedAmPm }) {
                    Text(
                        text = selectedAmPm, color = Brown,
                        fontSize = 16.sp
                    )
                }
                DropdownMenu(
                    expanded = expandedAmPm,
                    onDismissRequest = { expandedAmPm = false },
                    modifier = Modifier
                        .heightIn(max = 200.dp)
                        .background(Brown)
                ) {
                    amPmList.forEach { period ->
                        DropdownMenuItem(
                            onClick = {
                                selectedAmPm = period
                                expandedAmPm = false
                            },
                            text = {
                                Text(
                                    text = period, color = White,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}