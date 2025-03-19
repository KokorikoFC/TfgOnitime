package com.example.tfgonitime.ui.components.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MessageInput(onSend: (String) -> Unit) {
    var userInput by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color(0xFFF0F0F0), RoundedCornerShape(50)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = userInput,
            onValueChange = { userInput = it },
            placeholder = { Text("Escribe un mensaje...") },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            maxLines = 3, // Evita que crezca demasiado
            shape = RoundedCornerShape(20.dp) // Bordes más redondeados
        )

        IconButton(
            onClick = {
                if (userInput.isNotBlank()) {
                    onSend(userInput)
                    userInput = ""
                }
            }
        ) {
            Icon(imageVector = Icons.Default.Send, contentDescription = "Enviar")
        }
    }
}
