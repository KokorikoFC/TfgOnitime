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
    /*//Función para calcular si es un día consecutivo
    private fun isConsecutiveDay(lastLoginDate: Timestamp): Boolean {
        val lastLoginMillis = lastLoginDate.toDate().time
        val currentMillis = System.currentTimeMillis()

        //Calcular la diferencia de días entre las fechas
        val diffInDays = TimeUnit.MILLISECONDS.toDays(currentMillis - lastLoginMillis)

        //Es consecutivo si la diferencia es de 1 día
        return diffInDays == 1.toLong()

    }*/

    //Función para actualizar el streak
    fun updateStreak(currentStreak: Streak.Streak): Streak.Streak {
        val lastLoginDate = currentStreak.lastLoginDate

        return if (isNewDay(lastLoginDate)) {
            val newCheckInCount = currentStreak.checkInCount + 1

            val newStreakCount = if (newCheckInCount % 7 == 0){
                currentStreak.streakCount + 1
            } else {
                currentStreak.streakCount
            }
            //Actualizar el streak
            currentStreak.copy(
                streakCount = newStreakCount,
                lastLoginDate = Timestamp.now(), //Actualizar la fecha de último login
                checkInCount = newCheckInCount
            )
        }else{
            //Si no es un nuevo día, no se actualiza el streak
            currentStreak
        }

    }
}
