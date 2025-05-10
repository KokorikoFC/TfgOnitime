package com.example.tfgonitime.data.model

import java.util.UUID

// Assuming Task remains the same
data class Task(
    var id: String = "",
    val title: String = "",
    val description: String = "",
    val groupId: String? = null,
    val days: List<String> = emptyList(), // Note: Task also has days, maybe distinction needed?
    val completed: Boolean = false,
    val reminder: Reminder? = null
)


data class Reminder(
    val isSet: Boolean = false, // Changed from Long to Boolean for clarity
    val time: String? = null,  // Hora del recordatorio (formato HH:mm)
    val days: List<String> = emptyList() // Días para el recordatorio (ej: "Lunes", "Martes")
)