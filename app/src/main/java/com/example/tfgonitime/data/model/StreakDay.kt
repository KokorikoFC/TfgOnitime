package com.example.tfgonitime.data.model

data class StreakDay(
    var completed: Boolean = false, // ¿El usuario abrió la app ese día dentro del periodo de 7 días?
    var petId: String? = null,         // ID de la mascota activa ese día (puede ser null si no hay mascota)
    var reward: String? = null         // Recompensa obtenida ese día (puede ser null si no hay recompensa)
)

