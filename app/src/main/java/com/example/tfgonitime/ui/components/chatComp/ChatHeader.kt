package com.example.tfgonitime.ui.components.chatComp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tfgonitime.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHeader() {
    TopAppBar(
        modifier = Modifier
            .drawBehind {
                val strokeWidth = 2.dp.toPx()
                drawLine(
                    color = Color.Black,
                    start = Offset(0f, size.height), // Línea comienza en la esquina inferior izquierda
                    end = Offset(size.width, size.height), // Línea termina en la esquina inferior derecha
                    strokeWidth = strokeWidth
                )
            }
            .fillMaxWidth(),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White // Hace que el TopAppBar sea completamente blanco
        ),
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.head_daifuku),
                    contentDescription = "Onigiri Logo",
                    modifier = Modifier
                        .size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Oni Chat", fontSize = 26.sp, fontWeight = FontWeight.Bold)
            }
        }
    )
}
