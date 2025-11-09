package com.example.tfgonitime.ui.components.diaryComp

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.tfgonitime.R
import com.example.tfgonitime.ui.theme.Brown

@Composable
fun MoodOptions(
    selectedMood: MutableState<String>,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {

        // Primera opción
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    selectedMood.value = "deprimido"
                }
        ) {
            val isSelected = selectedMood.value == "deprimido"

            Image(
                painter = painterResource(id = R.drawable.emotionface_verysad),
                contentDescription = stringResource(R.string.mood_option_5),
                modifier = Modifier
                    .size(56.dp)
                    .alpha(if (isSelected) 1f else 0.5f)
                    .clip(CircleShape)
                    .background(
                        color = if (isSelected) Brown.copy(alpha = 0.3f) else Color.Transparent
                    )
                    .padding(8.dp)
            )
            Text(
                text = stringResource(R.string.mood_option_5),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else Brown.copy(
                        alpha = 0.7f
                    )
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Segunda opción
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    selectedMood.value = "triste"
                }
        ) {
            val isSelected = selectedMood.value == "triste"

            Image(
                painter = painterResource(id = R.drawable.emotionface_sad),
                contentDescription = stringResource(R.string.mood_option_4),
                modifier = Modifier
                    .size(56.dp)
                    .alpha(if (isSelected) 1f else 0.5f)
                    .clip(CircleShape)
                    .background(
                        color = if (isSelected) Brown.copy(alpha = 0.3f) else Color.Transparent
                    )
                    .padding(8.dp)
            )
            Text(
                text = stringResource(R.string.mood_option_4),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else Brown.copy(
                        alpha = 0.7f
                    )
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Tercera opción
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    selectedMood.value = "masomenos"
                }
        ) {
            val isSelected = selectedMood.value == "masomenos"

            Image(
                painter = painterResource(id = R.drawable.emotionface_neutral),
                contentDescription = stringResource(R.string.mood_option_3),
                modifier = Modifier
                    .size(56.dp)
                    .alpha(if (isSelected) 1f else 0.5f)
                    .clip(CircleShape)
                    .background(
                        color = if (isSelected) Brown.copy(alpha = 0.3f) else Color.Transparent
                    )
                    .padding(8.dp)
            )
            Text(
                text = stringResource(R.string.mood_option_3),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else Brown.copy(
                        alpha = 0.7f
                    )
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Cuarta opción
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    selectedMood.value = "feliz"
                }
        ) {
            val isSelected = selectedMood.value == "feliz"

            Image(
                painter = painterResource(id = R.drawable.emotionface_happy),
                contentDescription = stringResource(R.string.mood_option_2),
                modifier = Modifier
                    .size(56.dp)
                    .alpha(if (isSelected) 1f else 0.5f)
                    .clip(CircleShape)
                    .background(
                        color = if (isSelected) Brown.copy(alpha = 0.3f) else Color.Transparent
                    )
                    .padding(8.dp)
            )
            Text(
                text = stringResource(R.string.mood_option_2),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else Brown.copy(
                        alpha = 0.7f
                    )
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Quinta opción
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable(
                    indication = null, // Eliminar indicación de clic
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    selectedMood.value = "fantastico"
                }
        ) {
            val isSelected = selectedMood.value == "fantastico"

            Image(
                painter = painterResource(id = R.drawable.emotionface_veryhappy),
                contentDescription = stringResource(R.string.mood_option_1),
                modifier = Modifier
                    .size(56.dp)
                    .alpha(if (isSelected) 1f else 0.5f) // Ajustar opacidad
                    .clip(CircleShape)
                    .background(
                        color = if (isSelected) Brown.copy(alpha = 0.3f) else Color.Transparent
                    )
                    .padding(8.dp)
            )
            Text(
                text = stringResource(R.string.mood_option_1),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else Brown.copy(
                        alpha = 0.7f
                    )
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

    }
}
