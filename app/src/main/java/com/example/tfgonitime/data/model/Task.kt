package com.example.tfgonitime.data.model

import java.util.UUID

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val completed: Boolean,
    val createdAt: Long = System.currentTimeMillis()
)

