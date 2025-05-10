package com.example.tfgonitime.ui.components.taskComp

import android.widget.TimePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tfgonitime.ui.theme.Brown
import com.example.tfgonitime.ui.theme.DarkBrown
import com.example.tfgonitime.ui.theme.White
import java.util.*

@Composable
fun ReminderTimePicker(
    selectedTime: Long?,
    onTimeSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // Para mostrar la hora seleccionada en el formato hh:mm AM/PM
    val hours = (selectedTime?.div(100))?.toInt() ?: 12
    val minutes = (selectedTime?.rem(100))?.toInt() ?: 0
    val amPm = if (hours >= 12) "PM" else "AM"

    val formattedTime = String.format("%02d:%02d %s", hours % 12, minutes, amPm)

    // Estado para manejar la visibilidad de los dropdowns
    var expandedHour by remember { mutableStateOf(false) }
    var expandedMinute by remember { mutableStateOf(false) }
    var expandedAmPm by remember { mutableStateOf(false) }

    // Listas de valores posibles
    val hoursList = (1..12).toList()
    val minutesList = (0..59).toList()
    val amPmList = listOf("AM", "PM")

    // Estado de los valores seleccionados
    var selectedHour by remember { mutableStateOf(hours) }
    var selectedMinute by remember { mutableStateOf(minutes) }
    var selectedAmPm by remember { mutableStateOf(amPm) }

    // Función para actualizar la hora seleccionada
    fun updateTime() {
        // Convertimos a formato HHmm (24 horas)
        val hourIn24 =
            if (selectedAmPm == "PM" && selectedHour != 12) selectedHour + 12 else selectedHour
        val formattedTimeInMillis = (hourIn24 * 100 + selectedMinute).toLong()
        onTimeSelected(formattedTimeInMillis)
    }

    // Mostrar los botones de hora, minuto y AM/PM
    Column(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Dropdown para la hora
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
                        text = String.format("%02d", selectedHour),
                        color = Brown,
                        fontSize = 16.sp
                    )
                }
                DropdownMenu(
                    expanded = expandedHour,
                    onDismissRequest = { expandedHour = false },
                    modifier = Modifier
                        .heightIn(max = 200.dp)// Altura máxima del menú
                        .background(Brown)
                ) {
                    hoursList.forEach { hour ->
                        DropdownMenuItem(
                            onClick = {
                                selectedHour = hour
                                expandedHour = false
                                updateTime()
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
                        .heightIn(max = 200.dp) // Altura máxima del menú
                        .background(Brown)
                ) {
                    minutesList.forEach { minute ->
                        DropdownMenuItem(
                            onClick = {
                                selectedMinute = minute
                                expandedMinute = false
                                updateTime()
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
                        .heightIn(max = 200.dp) // Altura máxima del menú
                        .background(Brown)
                ) {
                    amPmList.forEach { period ->
                        DropdownMenuItem(
                            onClick = {
                                selectedAmPm = period
                                expandedAmPm = false
                                updateTime()
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
