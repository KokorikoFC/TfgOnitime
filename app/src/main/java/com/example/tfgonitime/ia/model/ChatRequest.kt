package com.example.tfgonitime.ia.model

data class ChatRequest(
    val model: String = "gpt-4",
    val messages: List<Message>
)

data class Message(
    val role: String,  // "user", "assistant" o "system"
    val content: String
)
