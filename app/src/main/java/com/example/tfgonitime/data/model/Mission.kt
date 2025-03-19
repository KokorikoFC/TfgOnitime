package com.example.tfgonitime.data.model


data class Mission(
    val id: String="",
    val description: String="",
    val reward: Int=0,
    val imageUrl: String="",
    val isCompleted: Boolean = false,
    val triggerAction: String = ""
)