package com.example.tfgonitime.data.model

data class User(
    val userId: String = "",
    val email: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
