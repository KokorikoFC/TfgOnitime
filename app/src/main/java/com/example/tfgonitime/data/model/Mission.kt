package com.example.tfgonitime.data.model

data class Mission(
    val id: String = "",
    val descriptionKey: String = "",
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false,
    val triggerAction: String = "",
    val imageUrl: String = "",
    val reward: Int = 0,
)