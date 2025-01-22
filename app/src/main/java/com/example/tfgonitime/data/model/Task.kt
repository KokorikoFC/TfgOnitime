package com.example.tfgonitime.data.model

import java.util.UUID

data class Task(
    val id: String = "", // Añadir valores por defecto
    val title: String = "",
    val description: String = "",
    val completed: Boolean = false
) {
    // Constructor sin argumentos
    constructor() : this("", "", "", false)
}


