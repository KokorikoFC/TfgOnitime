package com.example.tfgonitime.ui.components.taskComp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tfgonitime.ui.theme.*


@Composable
fun DaysOfWeekSelector(
    daysOfWeek: List<String> = listOf("L", "M", "X", "J", "V", "S", "D"),
    selectedDays: List<String>,
    onDaySelected: (String) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, Brown, RoundedCornerShape(8.dp))
        .clip(RoundedCornerShape(8.dp))) {

        Text(
            text = "Repetir",
            modifier = Modifier
                .fillMaxWidth()
                .background(Green)
                .padding(8.dp),
            color = White,
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            daysOfWeek.forEach { day ->
                DayChip(
                    text = day,
                    selected = selectedDays.contains(day),
                    onClick = {
                        onDaySelected(day)
                    }
                )
            }
        }
    }
}


@Composable
fun DayChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .clickable { onClick() }
            .width(35.dp)  // Ajusta el tamaño según lo que necesites
            .height(35.dp)  // Asegúrate de que sea cuadrado para que sea un círculo
            .border(
                width = 1.dp,
                color = if (selected) Green else Brown,
                shape = CircleShape
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Green else White,  // Color de fondo dependiendo de si está seleccionado
            contentColor = White,                // Color del contenido del Card (texto, iconos, etc.)
            disabledContainerColor = White,      // Color cuando el Card está deshabilitado
            disabledContentColor = Brown        // Color del contenido cuando está deshabilitado
        ),
        shape = CircleShape,

        ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                color = if (selected) Color.White else Color.Black,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}