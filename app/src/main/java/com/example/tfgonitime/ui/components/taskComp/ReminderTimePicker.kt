package com.example.tfgonitime.ui.components.taskComp

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessAlarm
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar


@Composable
fun ReminderTimePicker(
    selectedTime: Long?,
    onTimeSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {


    // Para mostrar la hora seleccionada
    val timeText = if (selectedTime != null) {
        // Convertimos el tiempo seleccionado en formato HH:mm
        val hours = (selectedTime / 100).toInt()
        val minutes = (selectedTime % 100).toInt()
        String.format("%02d:%02d", hours, minutes)
    } else {
        "Selecciona la hora"
    }

    val openDialog = remember { mutableStateOf(false) }


    // Mostramos el campo de texto con la hora seleccionada
    Box(modifier=Modifier.fillMaxWidth().clickable(onClick = { openDialog.value = true }),){
        IconButton(onClick = { openDialog.value = true }) {
            Icon(Icons.Default.AccessAlarm, contentDescription = "Seleccionar hora")
        }
        Text(text=timeText)
    }

    // Abrir el TimePickerDialog al pulsar
    if (openDialog.value) {
        TimePickerDialog(
            onDismissRequest = { openDialog.value = false },
            onTimeSelected = { hour, minute ->
                // Calculamos el valor en formato HHMM
                val selectedTimeInMillis = (hour * 100 + minute).toLong()
                onTimeSelected(selectedTimeInMillis) // Actualizamos el estado
                openDialog.value = false
            }
        )
    }
}

@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onTimeSelected: (Int, Int) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // TimePickerDialog
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            onTimeSelected(hourOfDay, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    // Mostrar el dialogo
    timePickerDialog.setOnDismissListener { onDismissRequest() }
    timePickerDialog.show()
}
