package com.example.tfgonitime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tfgonitime.ui.theme.Green

@Composable
fun ToggleTab(record: MutableState<Boolean>) {

    var isSelected by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFEFEFEF)) // Fondo gris claro
            .padding(0.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(3.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (!isSelected) Green else Color.Transparent)
                .clickable {
                    isSelected = false
                    record.value = false  // Establece record a true al hacer clic
                }
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Calendario",
                color = if (!isSelected) Color.White else Color.Black,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(3.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) Green else Color.Transparent)
                .clickable {
                    isSelected = true
                    record.value = true  // Establece record a true al hacer clic
                } // Cambiar estado al hacer clic
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Historial",
                color = if (isSelected) Color.White else Color.Black,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

