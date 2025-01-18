package com.example.tfgonitime.data.model

data class Mood(
    val userId: String,
    val moodDate: String,
    val moodType: String,
    val diaryEntry: String,
    val generatedLetter: String? = null // Texto generado por la IA
)
