package com.example.tfgonitime.ui.screens.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.example.tfgonitime.ui.components.CustomBottomNavBar
import com.example.tfgonitime.ui.components.chat.ChatBubble
import com.example.tfgonitime.ui.components.chat.MessageInput
import com.example.tfgonitime.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth


@Composable
fun ChatScreen(navHostController: NavHostController, chatViewModel: ChatViewModel) {
    val messages by chatViewModel.messages.collectAsState() // Ahora usamos messages

    Scaffold(
        containerColor = Color.White,
        bottomBar = { CustomBottomNavBar(navHostController) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Bottom // Asegura que los nuevos mensajes aparezcan al final
                ) {
                    items(messages) { message ->
                        ChatBubble(message)
                    }
                }

                MessageInput { userInput ->
                    chatViewModel.sendMessage(FirebaseAuth.getInstance().currentUser?.uid ?: "", userInput)
                }
            }
        }
    )
}