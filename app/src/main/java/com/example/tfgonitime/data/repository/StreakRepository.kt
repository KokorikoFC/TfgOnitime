package com.example.tfgonitime.data.repository

import com.example.tfgonitime.data.model.Streak
import com.google.firebase.Timestamp
import java.util.Calendar
import java.util.concurrent.TimeUnit

class StreakRepository {
    //Función para calcular si es un nuevo día
    private fun isNewDay(lastLoginDate: Timestamp): Boolean {
        val lastLoginMillis = lastLoginDate.toDate().time
        val currentMillis = System.currentTimeMillis()

        //Calcular la diferencia de días entre las fechas
        val diffInDays = TimeUnit.MILLISECONDS.toDays(currentMillis - lastLoginMillis)

        return diffInDays >= 1 //Si la diferencia es mayor o igual a 1, es un nuevo día

    }
    //Función para calcular si es un día consecutivo
    private fun isConsecutiveDay(lastLoginDate: Timestamp): Boolean {
        val lastLoginMillis = lastLoginDate.toDate().time
        val currentMillis = System.currentTimeMillis()

        //Calcular la diferencia de días entre las fechas
        val diffInDays = TimeUnit.MILLISECONDS.toDays(currentMillis - lastLoginMillis)

        //Es consecutivo si la diferencia es de 1 día
        return diffInDays == 1.toLong()

    }

    /*//Función para actualizar el streak
    fun updateStreak(currentStreak: Streak.Streak): Streak.Streak {
        val lastLoginDate = currentStreak.lastLoginDate

        return if (isNewDay(lastLoginDate)) {
            //Si es un nuevo día, se actualiza el streak
            val newCheckInCount = if (isConsecutiveDay(lastLoginDate)) {
                currentStreak.checkInCount + 1
            } else {
                1
            }
        }
    }*/
}