package com.example.tfgonitime.data.repository

import com.example.tfgonitime.data.model.Streak
import com.google.firebase.Timestamp
import java.util.Calendar
import java.util.concurrent.TimeUnit

class StreakRepository {
    //Función para calcular si es un nuevo día (Basado en el calendario)
    private fun isNewDay(lastLoginDate: Timestamp): Boolean {
        val lastLoginCalendar = Calendar.getInstance().apply {
            time = lastLoginDate.toDate()
        }
        val lastDay = lastLoginCalendar.get(Calendar.DAY_OF_YEAR)
        val lastYear = lastLoginCalendar.get(Calendar.YEAR)

        //Obtener el día, mes y año actual
        val currentCalendar = Calendar.getInstance()
        val currentDay = currentCalendar.get(Calendar.DAY_OF_YEAR)
        val currentYear = currentCalendar.get(Calendar.YEAR)

        //Es un nuevo día si el año o el día del año son diferentes
        return currentYear > lastYear || currentDay > lastDay
    }

    private fun isConsecutiveDay(lastLoginDate: Timestamp): Boolean{
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_YEAR)

        //Obtener el día y año del último login
        calendar.time = lastLoginDate.toDate()
        val lastDay = calendar.get(Calendar.DAY_OF_YEAR)

        //Obtener el día y año actual
        val currentYear = calendar.get(Calendar.YEAR)
        val lastYear = calendar.get(Calendar.YEAR)

        return (currentYear == lastYear && currentDay == lastDay + 1) ||
                (currentYear == lastYear + 1 && currentDay == 1 && lastDay == 365)
    }

    //Función para actualizar el streak
    fun updateStreak(currentStreak: Streak.Streak): Streak.Streak {
        val lastLoginDate = currentStreak.lastLoginDate

        return if (isNewDay(lastLoginDate)) {
            // Si es un nuevo día, actualiza la racha
            val newCheckInCount = if (isConsecutiveDay(lastLoginDate)) {
                currentStreak.checkInCount + 1
            } else {
                1 // Racha rota, se reinicia
            }
            val newStreakCount = if (newCheckInCount == 7) {
                currentStreak.streakCount + 1 // Racha completada
            } else {
                currentStreak.streakCount
            }

            //Actualizar el streak
            currentStreak.copy(
                streakCount = newStreakCount, //Actualizar el contador de racha
                lastLoginDate = Timestamp.now(), //Actualizar la fecha de último login
                checkInCount = newCheckInCount //Actualizar el contador de check-in
            )
        }else{
            //Si no es un nuevo día, no se actualiza el streak
            currentStreak
        }

    }
}
