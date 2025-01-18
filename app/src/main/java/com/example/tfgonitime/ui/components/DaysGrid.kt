package com.example.tfgonitime.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.painterResource
import com.example.tfgonitime.viewmodel.DiaryViewModel

@Composable
fun DaysGrid(
    date: LocalDate,
    isSelected: Boolean,
    emojiResId: Int?,
    onDaySelected: (LocalDate) -> Unit
) {

    val currentDate = LocalDate.now() // Fecha actual

    Box(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onDaySelected(date) },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Emoji o círculo vacío
            if (emojiResId != null) {
                Image(
                    painter = painterResource(id = emojiResId),
                    contentDescription = "Estado de ánimo",
                    modifier = Modifier.size(30.dp)
                )
            } else {
                // Cambiar el color si el día es el actual
                val circleColor = when {
                    date == currentDate -> Color(0xFF2196F3) // Azul para el día actual
                    isSelected -> Color(0xFF008000) // Verde para el día seleccionado
                    else -> Color(0xFFEFEFEF) // Gris claro para otros días
                }

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(circleColor)
                )
            }

            // Número del día
            Text(
                text = "${date.dayOfMonth}",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                color = Color.Black
            )
        }
    }
}
