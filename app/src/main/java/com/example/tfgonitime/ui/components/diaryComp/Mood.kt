package com.example.tfgonitime.ui.components.diaryComp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tfgonitime.R
import com.example.tfgonitime.data.model.Mood
import com.example.tfgonitime.ui.screens.diary.formatDateForDisplay
import com.example.tfgonitime.ui.theme.Brown
import com.example.tfgonitime.ui.theme.LightBeige
import com.example.tfgonitime.ui.theme.Green

@Composable
fun Mood(mood: Mood, onMoreClick: (Mood) -> Unit, navController: NavController) {

    val emojiResId = mapOf (
        "fantastico" to R.drawable.emotionface_veryhappy,
        "feliz" to R.drawable.emotionface_happy,
        "masomenos" to R.drawable.emotionface_neutral,
        "triste" to R.drawable.emotionface_sad,
        "deprimido" to R.drawable.emotionface_verysad,
    )

    val resourceId = emojiResId[mood.moodType]?: R.drawable.emotionface_happy

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column {
            // Fila superior: moodDate, Leer carta de apoyo e ícono
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatDateForDisplay(mood.moodDate),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onTertiary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Box(
                    modifier = Modifier
                        .border(1.dp, Green, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            navController.navigate("letterScreen/${mood.moodDate}")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Email,
                            contentDescription = "Icono de email",
                            tint = Green,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.mood_letter),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = null,
                    tint = Brown,
                    modifier = Modifier.clickable(
                        indication = null, // Eliminar indicación de clic
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onMoreClick(mood)
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Fila principal con dos columnas
            Row(
                modifier = Modifier
                    .background(Brown.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Columna 1: Imagen y moodType
                Column(
                    modifier = Modifier
                        .weight(0.3f)
                        .padding(end = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = resourceId),
                        contentDescription = null,
                        modifier = Modifier.size(35.dp)
                    )
                    Text(
                        text = ComprobarMoodType(mood.moodType),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onTertiary
                        ),
                        textAlign = TextAlign.Center, // Centra el texto
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )

                }

                // Columna 2: diaryEntry
                Column(
                    modifier = Modifier
                        .weight(0.7f)
                        .padding(start = 8.dp)
                ) {
                    Text(
                        text = mood.diaryEntry.truncateWords(20), // Limitar palabras
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onPrimary
                        ),
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

fun String.truncateWords(wordLimit: Int): String {
    val words = this.split(" ")
    return if (words.size > wordLimit) {
        words.take(wordLimit).joinToString(" ") + "..."
    } else {
        this
    }
}

@Composable
fun ComprobarMoodType(moodType: String): String {
    if (moodType == "fantastico") {
        return stringResource(R.string.mood_option_1)
    } else if (moodType == "feliz") {
        return stringResource(R.string.mood_option_2)
    } else if (moodType == "masomenos") {
        return stringResource(R.string.mood_option_3)
    } else if (moodType == "triste") {
        return stringResource(R.string.mood_option_4)
    } else if (moodType == "deprimido") {
        return stringResource(R.string.mood_option_5)
    } else {
        return "No se ha podido determinar el estado de ánimo"
    }
}