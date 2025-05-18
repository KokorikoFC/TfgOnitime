package com.example.tfgonitime.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgonitime.BuildConfig
import com.example.tfgonitime.ia.ChatRepository
import com.example.tfgonitime.ia.api.RetrofitClient
import com.example.tfgonitime.ia.model.ChatRequest
import com.example.tfgonitime.ia.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class ChatGptViewModel : ViewModel() {

    private val chatRepository = ChatRepository()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> get() = _messages

    fun sendMessage(userId: String, userMessage: String) {
        if (userMessage.isBlank()) return

        viewModelScope.launch {
            val newMessage = ChatMessage(userId, userMessage)
            _messages.value = _messages.value + newMessage

            try {
                val responseText = chatRepository.sendMessageChat(userId, userMessage)
                val aiMessage = ChatMessage("AI", responseText)
                _messages.value = _messages.value + aiMessage
            } catch (e: Exception) {
                val errorMessage = ChatMessage("AI", "Error: ${e.message}")
                _messages.value = _messages.value + errorMessage
            }
        }
    }
}
