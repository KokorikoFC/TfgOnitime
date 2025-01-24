package com.example.tfgonitime.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tfgonitime.R

@Composable
fun MoodOptions(
    selectedMood: MutableState<String>
) {
    // Selección de moodType
    val moodOptions = listOf(
        R.drawable.happy_face to "Fantástico",
        R.drawable.happy_face to "Feliz",
        R.drawable.happy_face to "Más o menos",
        R.drawable.happy_face to "Cansado",
        R.drawable.happy_face to "Deprimido"
    )

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        moodOptions.forEachIndexed { index, (emojiResId, label) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable(
                        indication = null, // Eliminar indicación de clic
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        selectedMood.value = label
                    }
            ) {
                val isSelected = selectedMood.value == label

                Image(
                    painter = painterResource(id = emojiResId),
                    contentDescription = label,
                    modifier = Modifier
                        .size(56.dp)
                        .alpha(if (isSelected) 1f else 0.5f) // Ajustar opacidad
                        .clip(CircleShape)
                        .background(
                            color = if (isSelected) Color(0xFFF5F5F5) else Color.Transparent
                        )
                        .padding(8.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall.copy(color = if (isSelected) Color.Black else Color.Gray),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
