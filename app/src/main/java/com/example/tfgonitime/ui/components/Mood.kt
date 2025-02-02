package com.example.tfgonitime.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tfgonitime.R
import com.example.tfgonitime.ui.theme.Green
import com.google.type.Date

@Composable
fun Mood(moodDate: String, moodType: String, diaryEntry: String) {
    Box(
        modifier = Modifier
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
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
                    text = moodDate,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = "Leer carta de apoyo",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Green
                    ),
                    modifier = Modifier
                        .border(1.dp, Green, RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = Color.Gray
                )

            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fila principal con dos columnas
            Row(
                modifier = Modifier
                    .border(1.dp, Color.LightGray)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Columna 1: Imagen y moodType
                Column(
                    modifier = Modifier
                        .border(1.dp, Color.LightGray)
                        .weight(0.3f)
                        .padding(end = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.happy_face), // Cambia por tu ícono
                        contentDescription = null,
                        modifier = Modifier.size(35.dp)
                    )
                    Text(
                        text = moodType,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Columna 2: diaryEntry
                Column(
                    modifier = Modifier
                        .border(1.dp, Color.LightGray)
                        .weight(0.7f)
                        .padding(start = 8.dp)
                ) {
                    Text(
                        text = diaryEntry,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.DarkGray
                        ),
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}


