package com.example.tfgonitime.data.model

import java.time.LocalDate

data class User(
    val userId: String = "",
    val userName: String = "",
    val birthDate: LocalDate? = null,
    val gender: String = "",
    val email: String = "",
    val actualLevel: Int = 0,
    val coins: Int = 0,
    val tasksCompleted: Int = 0,
    val darkModePreference: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

