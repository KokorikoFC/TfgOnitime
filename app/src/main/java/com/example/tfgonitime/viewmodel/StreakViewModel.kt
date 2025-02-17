package com.example.tfgonitime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgonitime.data.model.Streak
import com.example.tfgonitime.data.repository.StreakRepository
import com.example.tfgonitime.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import com.google.firebase.Timestamp
import java.util.Calendar
import java.util.Date
import java.util.TimeZone


class StreakViewModel : ViewModel() {
    private val streakRepository = StreakRepository()

    private val _streakState = MutableStateFlow<Streak?>(null)
    val streakState: StateFlow<Streak?> = _streakState

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    fun loadStreak(userId: String) {
        viewModelScope.launch {
            _loadingState.value = true
            val result = streakRepository.getStreak(userId)
            _loadingState.value = false

            result.onSuccess { streak ->
                _streakState.value = streak
            }.onFailure {
                println("Error al cargar la racha: ${it.message}")
            }
        }
    }


    fun markDayCompleted(userId: String, dayOfWeek: DayOfWeek, petId: String?, reward: String?) { // Permite petId y reward nulos
        viewModelScope.launch {
            val result = streakRepository.markDayCompleted(userId, dayOfWeek, petId, reward)
            result.onSuccess {
                loadStreak(userId) // Recargar la racha general después de marcar el día
            }.onFailure {
                println("Error al marcar el día: ${it.message}")
            }
        }
    }


    fun updateStreakOnLogin(userId: String) {
        viewModelScope.launch {
            _loadingState.value = true
            val currentStreakData = streakRepository.getStreak(userId).getOrNull() ?: Streak()

            val updatedStreak = calculateUpdatedStreak(currentStreakData)

            val result = streakRepository.updateStreak(userId, updatedStreak)
            _loadingState.value = false

            result.onSuccess {
                loadStreak(userId)
            }.onFailure {
                println("Error al actualizar la racha: ${it.message}")
            }
        }
    }


    private fun calculateUpdatedStreak(currentStreakData: Streak): Streak {
        val currentDate = LocalDate.now(ZoneId.of("Europe/Madrid")) // Zona horaria de Madrid
        val lastCheckInTimestamp = currentStreakData.lastCheckIn


        if (lastCheckInTimestamp == null) {
            // Primera vez que inicia sesión o racha no inicializada
            return currentStreakData.copy(
                currentStreak = 1,
                lastCheckIn = Timestamp(Date.from(currentDate.atStartOfDay(ZoneId.of("Europe/Madrid")).toInstant())), // Timestamp de hoy en Madrid
                longestStreak = maxOf(currentStreakData.longestStreak, 1) // Actualizar longestStreak si es la primera vez
            )
        }


        val lastCheckInDate = lastCheckInTimestamp.toDate().toInstant().atZone(ZoneId.of("Europe/Madrid")).toLocalDate()


        val daysSinceLastLogin = ChronoUnit.DAYS.between(lastCheckInDate, currentDate)


        return when {
            daysSinceLastLogin == 0L -> {
                // Ya inició sesión hoy, no hacer nada con la racha, solo actualizar lastCheckIn para que sea la última vez hoy
                currentStreakData.copy(lastCheckIn = Timestamp(Date.from(currentDate.atStartOfDay(ZoneId.of("Europe/Madrid")).toInstant())))
            }
            daysSinceLastLogin <= 7 -> {
                // Inició sesión dentro de los últimos 7 días, ¡mantiene la racha!
                val newStreak = currentStreakData.currentStreak + 1
                currentStreakData.copy(
                    currentStreak = newStreak,
                    lastCheckIn = Timestamp(Date.from(currentDate.atStartOfDay(ZoneId.of("Europe/Madrid")).toInstant())), // Actualizar lastCheckIn a hoy
                    longestStreak = maxOf(currentStreakData.longestStreak, newStreak) // Actualizar longestStreak si es necesario
                )
            }
            else -> {
                // Inició sesión hace más de 7 días, ¡racha rota!
                Streak(
                    currentStreak = 1, // Reiniciar racha a 1
                    lastCheckIn = Timestamp(Date.from(currentDate.atStartOfDay(ZoneId.of("Europe/Madrid")).toInstant())), // lastCheckIn a hoy
                    longestStreak = currentStreakData.longestStreak // longestStreak se mantiene
                )
            }
        }
    }


}