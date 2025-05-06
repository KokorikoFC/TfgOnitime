package com.example.tfgonitime.ui.components.chatComp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tfgonitime.ui.theme.Green

@Composable
fun MessageInput(onSend: (String) -> Unit) {
    var userInput by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = userInput,
            onValueChange = { userInput = it },
            placeholder = { Text("Message", color = Color.Gray) },
            modifier = Modifier
                .weight(1f)
                .height(54.dp)
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(17.dp))
                .shadow(1.dp, RoundedCornerShape(17.dp))
                .background(Color.White, RoundedCornerShape(17.dp)),
            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
            singleLine = true, // Solo una línea
            maxLines = 1,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = {
                if (userInput.isNotBlank()) {
                    onSend(userInput)
                    userInput = ""
                }
            },
            modifier = Modifier
                .size(48.dp)
                .background(Green, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Enviar",
                tint = Color.White
            )
        }
    }
}