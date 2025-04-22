package com.example.tfgonitime.ui.screens.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tfgonitime.ui.components.CustomBottomNavBar
import com.example.tfgonitime.ui.components.chatComp.ChatBubble
import com.example.tfgonitime.ui.components.chatComp.ChatHeader
import com.example.tfgonitime.ui.components.chatComp.MessageInput
import com.example.tfgonitime.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


@Composable
fun ChatScreen(navHostController: NavHostController, chatViewModel: ChatViewModel) {
    val messages by chatViewModel.messages.collectAsState() // Ahora usamos messages
    val listState = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()

    // Determina si el usuario no está al final de la lista
    val isAtBottom = remember {
        derivedStateOf {
            val lastIndex = messages.size - 1
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == lastIndex
        }
    }

    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(
                index = messages.size - 1,
                scrollOffset = 0 // Puedes ajustar este valor si lo necesitas
            )
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = { ChatHeader() },
        bottomBar = { CustomBottomNavBar(navHostController) },
        floatingActionButton = {
            if (messages.isNotEmpty() && !isAtBottom.value) {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(messages.size - 1)
                        }
                    },
                    modifier = Modifier
                        .padding(bottom = 70.dp, end = 4.dp)
                        .size(45.dp),
                    shape = CircleShape,
                    containerColor = Color(0x30000000), // Fondo con opacidad
                    elevation = FloatingActionButtonDefaults.elevation(0.dp) // Eliminar sombra
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardDoubleArrowDown, // Doble flecha hacia abajo
                        contentDescription = "Bajar al final",
                        tint = Color.White // Color del ícono
                    )
                }

            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .padding(paddingValues),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.Bottom // Asegura que los nuevos mensajes aparezcan al final
                ) {
                    items(messages) { message ->
                        ChatBubble(message)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                MessageInput { userInput ->
                    chatViewModel.sendMessage(FirebaseAuth.getInstance().currentUser?.uid ?: "", userInput)
                }
            }
        }
    )
}