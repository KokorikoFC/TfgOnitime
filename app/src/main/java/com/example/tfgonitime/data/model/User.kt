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
    val diaryEntryYear: Int = 0,
    val coinsYear: Int = 0,
    val messagesOniYear: Int = 0,
    val yearRef: Int = 0,
    val totalActiveDays: Int = 0,
    val lastActiveDate: String = "",
)