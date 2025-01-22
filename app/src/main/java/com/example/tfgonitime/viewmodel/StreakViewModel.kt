package com.example.tfgonitime.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tfgonitime.data.model.Streak
import com.example.tfgonitime.data.repository.StreakRepository

class StreakViewModel(private val streakRepository: StreakRepository) : ViewModel() {

    fun getCurrentStreak(): Streak {
        // Datos de FireBase, obtén los datos necesarios para crear un objeto Streak
        val streakData = // obtener datos de FireBase
            return Streak(streakData)
    }

    // Actualiza la racha y devuelve los datos actualizados
    fun updateUserStreak(): Streak.Streak {
        val currentStreak = getCurrentStreak()
        return streakRepository.updateStreak(currentStreak)
    }

}