package com.example.tfgonitime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CustomRadioButton(isSelected: Boolean, onClick: () -> Unit) {
    val borderColor = Color(0xFF7A7E40) // Verde oliva del borde
    val fillColor = if (isSelected) Color(0xFF7A7E40) else Color.Transparent

    Box(
        modifier = Modifier
            .size(24.dp)
            .clickable { onClick() }
            .background(color = Color.Transparent, shape = CircleShape)
            .border(width = 2.dp, color = borderColor, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color = fillColor, shape = CircleShape)
            )
        }
    }
}