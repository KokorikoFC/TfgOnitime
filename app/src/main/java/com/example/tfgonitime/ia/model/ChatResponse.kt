package com.example.tfgonitime.ia.model

data class ChatResponse(
    val id: String,
    val choices: List<Choice>
)

data class Choice(
    val index: Int,
    val message: Message
)
