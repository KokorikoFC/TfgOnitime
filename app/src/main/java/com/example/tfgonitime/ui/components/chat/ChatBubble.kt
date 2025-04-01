package com.example.tfgonitime.ui.components.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tfgonitime.R
import com.example.tfgonitime.ui.theme.Brown
import com.example.tfgonitime.ui.theme.DarkBrown
import com.example.tfgonitime.ui.theme.Green
import com.example.tfgonitime.viewmodel.ChatMessage
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.senderId == FirebaseAuth.getInstance().currentUser?.uid

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {

        // La burbuja de texto
        Box(
            modifier = Modifier
                .wrapContentWidth() // Ajusta el Box al contenido del texto
                .widthIn(min = 50.dp, max = 240.dp) // Limita el tamaño mínimo y máximo
                .background(
                    if (isUser) Green.copy(alpha = 0.5f) else Brown.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp) // Ajustamos padding
        ) {
            Text(
                text = message.text.trim(),
                fontSize = 16.sp,
                textAlign = TextAlign.Start,
                softWrap = true, // Permite que el texto se ajuste automáticamente
                modifier = Modifier.wrapContentWidth() // El texto solo ocupa el espacio necesario
            )
        }

    }
}


