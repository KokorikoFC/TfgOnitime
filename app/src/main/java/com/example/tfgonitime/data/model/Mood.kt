package com.example.tfgonitime.data.model

import java.util.UUID

data class Mood(
    val id: String = UUID.randomUUID().toString(),
    val moodDate: String = "", // Fecha en formato "YYYY-MM-DD",
    val moodType: String = "", // Tipo de mood (feliz, triste, etc.)
    val diaryEntry: String = "", // Entrada del diario
    val generatedLetter: String? = null // Texto generado por la IA
)