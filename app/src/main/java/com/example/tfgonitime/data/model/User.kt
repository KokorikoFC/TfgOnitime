package com.example.tfgonitime.data.model

data class User(
    val userName: String = "",
    val birthDate: Map<String, Int>? = null,
    val gender: String = "",
    val email: String = "",
    val actualLevel: Int = 0,
    val coins: Int = 0,
    val tasksCompleted: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val userPetId: String = "",
)

