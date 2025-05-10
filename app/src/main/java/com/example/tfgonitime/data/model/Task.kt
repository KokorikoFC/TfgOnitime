package com.example.tfgonitime.data.model

import com.google.firebase.Timestamp
import kotlinx.datetime.LocalDate
import java.util.UUID

data class Task(
    var id: String = "",
    val title: String = "",
    val description: String = "",
    val groupId: String? = null,
    val completed: Boolean = false,
    val reminder: Reminder? = null,
    val lastCompletedDate: Timestamp? = null
)


data class Reminder(
    val isSet: Boolean = true,
    val time: String? = null,
    val days: List<String> = emptyList()
)