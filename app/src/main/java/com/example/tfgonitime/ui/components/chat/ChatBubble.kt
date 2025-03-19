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
import com.example.tfgonitime.viewmodel.ChatMessage
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.senderId == FirebaseAuth.getInstance().currentUser?.uid

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Image(
                painter = painterResource(id = R.drawable.head_daifuku),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .padding(end = 8.dp)
            )
        }

        Box(
            modifier = Modifier
                .wrapContentWidth()
                .widthIn(min = 50.dp, max = 230.dp)
                .background(
                    if (isUser) Color(0xFFD1F7C4) else Color(0xFFEAEAEA),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp) // Ajustamos padding
        ) {
            Text(
                text = message.text.trim(), // 🔹 Elimina espacios en blanco al inicio y al final
                fontSize = 16.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.wrapContentWidth()
            )
        }
    }
}