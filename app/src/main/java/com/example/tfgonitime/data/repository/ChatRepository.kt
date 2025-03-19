package com.example.tfgonitime.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.example.tfgonitime.data.model.Mood

class ChatRepository {

    private val diaryRepository = DiaryRepository()
    private val conversationHistory = mutableMapOf<String, MutableList<String>>()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = "AIzaSyD6iIdvIEiwQgd-iG7gXyf7JAP1oXsBGlU"
    )

    suspend fun sendMessage(userId: String, userMessage: String): String {
        conversationHistory.getOrPut(userId) { mutableListOf() }
        conversationHistory[userId]?.add("Usuario: $userMessage")

        val history = conversationHistory[userId]?.takeLast(3) // Considerar los últimos 3 mensajes
        val context = buildContext(history)
        val messageWithContext = "$context\n\nUsuario: $userMessage"

        return try {
            val response = generativeModel.generateContent(messageWithContext)
            val aiResponse = response.text ?: "Sin respuesta"
            conversationHistory[userId]?.add("Oni: $aiResponse")
            aiResponse
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    private fun buildContext(history: List<String>?): String {
        val historyText = history?.joinToString("\n") ?: ""

        val greetingInstruction = if (history.isNullOrEmpty()) {
            "Saluda al usuario de forma amigable como Oni."
        } else {
            ""
        }

        return """
    **Personalidad:** Eres Oni, un amigo y compañero en una app de diario de estado de ánimo y gestión del tiempo. Tu rol es ser amigable, comprensivo y ofrecer apoyo emocional. Tienes conocimientos sobre bienestar emocional y técnicas para mejorar el estado de ánimo.

    **Tarea:** Mantener una conversación de apoyo con el usuario. Esto incluye $greetingInstruction Escucha activamente sus respuestas y ofrece ánimo o sugerencias relevantes basadas en sus emociones y el contexto de la app.

    **Objetivo:** Ayudar al usuario a sentirse comprendido, apoyado y animado. Queremos que la interacción con la app sea una experiencia positiva que contribuya a su bienestar emocional.

    **Guía sobre cómo responder:**
    - **Saludo:** $greetingInstruction
    - **Contexto de la conversación anterior:**
    $historyText
    - **Respuesta a sentimientos negativos:** Cuando el usuario exprese sentirse mal, triste o de otra manera negativa, reconoce su sentimiento con empatía. Pregúntale brevemente por qué se siente así. Intenta que tu respuesta sea concisa y directa.
    - **Ofrecer apoyo y sugerencias:** Basándote en cómo se siente el usuario, ofrece sugerencias para mejorar su estado de ánimo. Intenta que estas sugerencias sean prácticas y breves.
    - **Mantener la conversación:** Después de la respuesta inicial del usuario, intenta continuar la conversación mostrando interés de forma concisa y enfocada en el tema actual.
    - **Formato:** Escribe en texto plano, sin emojis ni formato markdown.

    **Información relevante:**
    - La app permite al usuario registrar su estado de ánimo y escribir una entrada de diario cuando lo desee.
    - También incluye funciones para la gestión del tiempo.
    """.trimIndent()
    }

}