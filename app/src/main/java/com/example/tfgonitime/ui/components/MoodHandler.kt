package com.example.tfgonitime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import com.example.tfgonitime.data.model.Mood


@Composable
fun MoodHandler(
    mood: Mood,
    navHostController: NavHostController,
    onEliminarClick: () -> Unit,
    onClose: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(
                indication = null, // Eliminar indicación de clic
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClose()
            } // Ocultar el componente al hacer clic fuera
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp
                    )
                )
                .padding(start = 16.dp, top = 16.dp, bottom = 40.dp)
                .clickable { /* Evitar que se cierre al hacer clic dentro */ }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        indication = null, // Eliminar indicación de clic
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        // Redirigir a MoodEditScreen pasando el mood
                        navHostController.navigate("moodEditScreen/${mood.moodDate}") {
                            launchSingleTop = true
                        }
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar Diario",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Editar Diario",
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        indication = null, // Eliminar indicación de clic
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onEliminarClick()
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar Diario",
                    tint = Color(0xFF8A8A5C),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Eliminar Diario",
                    color = Color(0xFF8A8A5C),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}



