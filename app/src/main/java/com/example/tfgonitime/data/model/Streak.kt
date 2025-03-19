package com.example.tfgonitime.data.model

import com.google.firebase.Timestamp

data class Streak(
    var currentStreak: Int = 0,  // Racha actual de periodos de 7 días
    var longestStreak: Int = 0,  // Racha más larga alcanzada en periodos de 7 días
    var lastCheckIn: com.google.firebase.Timestamp? = null // Última fecha de inicio de sesión (Timestamp)
)