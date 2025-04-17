package com.example.tfgonitime.data.model

import java.util.UUID



data class Task(
    var id: String = "",
    val title: String = "",
    val description: String = "",
    val groupId: String? = null,
    val days: List<String> = emptyList(),
    val completed: Boolean = false,
    val reminder: Reminder? = null
)


data class Reminder(
    val isSet: Long = 0L, // Si el recordatorio está activado
    val time: String? = null,  // Hora del recordatorio (formato HH:mm)
    val days: List<String> = emptyList() // Días para el recordatorio
)



