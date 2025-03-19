package com.example.tfgonitime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgonitime.data.repository.ChatRepository
import com.example.tfgonitime.data.repository.DiaryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val chatRepository = ChatRepository()

    // Lista de mensajes en lugar de solo un "response"
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> get() = _messages

    fun sendMessage(userId: String, userMessage: String) {
        if (userMessage.isBlank()) return

        viewModelScope.launch {
            // Agrega el mensaje del usuario
            val newMessage = ChatMessage(userId, userMessage)
            _messages.value = _messages.value + newMessage

            try {
                val responseText = chatRepository.sendMessage(userId, userMessage)
                val aiMessage = ChatMessage("AI", responseText)
                _messages.value = _messages.value + aiMessage
            } catch (e: Exception) {
                val errorMessage = ChatMessage("AI", "Error: ${e.message}")
                _messages.value = _messages.value + errorMessage
            }
        }
    }
}

data class ChatMessage(val senderId: String, val text: String)
