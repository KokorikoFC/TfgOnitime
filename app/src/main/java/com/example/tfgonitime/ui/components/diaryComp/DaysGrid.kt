package com.example.tfgonitime.ui.components.diaryComp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import com.example.tfgonitime.R

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
            .clickable (
                indication = null, // Eliminar indicación de clic
                interactionSource = remember { MutableInteractionSource() }
            ) { onDaySelected(date) },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (emojiResId != null) {
                Image(
                    painter = painterResource(id = emojiResId),
                    contentDescription = "Estado de ánimo",
                    modifier = Modifier.size(30.dp)
                )
            } else {
                // Si la fecha es la actual, mostrar imagen personalizada
                if (date == currentDate) {
                    Image(
                        painter = painterResource(id = R.drawable.emotionface_plus), // Cambia por tu imagen
                        contentDescription = "Día actual",
                        modifier = Modifier.size(30.dp)
                    )
                } else {
                    // Círculo vacío para otros días
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFEFEF))
                    )
                }
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
