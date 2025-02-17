package com.example.tfgonitime.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgonitime.data.model.Streak
import com.example.tfgonitime.data.model.StreakDay
import com.example.tfgonitime.data.repository.StreakRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StreakViewModel : ViewModel() {

    private val streakRepository = StreakRepository()  

    // Estado para la racha actual
    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak

    // Estado para la racha más larga
    private val _longestStreak = MutableStateFlow(0)
    val longestStreak: StateFlow<Int> = _longestStreak

    // Estado de carga (loading)
    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    fun loadStreak(userId: String) {
        viewModelScope.launch {
            _loadingState.value = true
            val streakResult = streakRepository.getStreak(userId)

            streakResult.onSuccess { streak ->
                _loadingState.value = false
                if (streak!= null) {
                    _currentStreak.value = streak.currentStreak
                    _longestStreak.value = streak.longestStreak
                }
            }.onFailure {
                _loadingState.value = false
                // Manejar el error
            }
        }
    }

    fun onOpenAppTodayClicked(userId: String) {
        viewModelScope.launch {
            _loadingState.value = true
            updateStreakForToday(userId)
            _loadingState.value = false
        }
    }

    private suspend fun updateStreakForToday(userId: String) {
        try {
            Log.d("StreakViewModel", "updateStreakForToday - Start")

            val streakResult = streakRepository.getStreak(userId)
            Log.d("StreakViewModel", "updateStreakForToday - After getStreakResult, success: ${streakResult.isSuccess}")

            streakResult.onSuccess { streak ->
                if (streak != null) {
                    Log.d("StreakViewModel", "updateStreakForToday - Streak before update: currentStreak=${streak.currentStreak}, longestStreak=${streak.longestStreak}")

                    streak.currentStreak = streak.currentStreak + 1
                    Log.d("StreakViewModel", "updateStreakForToday - After increment currentStreak: currentStreak=${streak.currentStreak}, longestStreak=${streak.longestStreak}")

                    if (streak.currentStreak > streak.longestStreak) {
                        streak.longestStreak = streak.currentStreak
                    }

                    if (streak.currentStreak > 7) {
                        streak.currentStreak = 1
                        resetStreakDays(userId)
                    } else {
                        if (streak.currentStreak > streak.longestStreak) {
                            streak.longestStreak = streak.currentStreak
                        } else {
                            // Si la racha actual no ha superado la racha más larga, no la cambiamos
                            // Pero si la racha más larga ya ha superado los 7 días, sigue incrementándose si lo deseas
                            if (streak.longestStreak >= 7) {
                                streak.longestStreak = streak.longestStreak + 1
                            }
                        }
                    }


                    streakRepository.updateStreak(userId, streak)

                    _currentStreak.value = streak.currentStreak
                    _longestStreak.value = streak.longestStreak

                } else {
                }
            }.onFailure { error ->
            }
        } catch (e: Exception) {
            Log.e("StreakViewModel", "updateStreakForToday - Exception: ${e.message}")
        }
    }



    fun updateStreak(userId: String, day: Int) {
        viewModelScope.launch {
            try {
                // Obtener la racha del usuario
                val streakResult = streakRepository.getStreak(userId)

                streakResult.onSuccess { streak ->
                    if (streak != null) {
                        // Agregar el día específico (por ejemplo, marcando el día como completado)
                        streakRepository.addDayToStreak(userId, day, StreakDay(completed = true))

                        // Actualizamos la racha
                        streak.currentStreak = day  // Actualizamos la racha actual
                        if (streak.currentStreak > streak.longestStreak) {
                            streak.longestStreak = day // Update the streak object
                            _longestStreak.value = day  // ✅ Corrected: Update _longestStreak StateFlow
                        }

                        // Si llega a 7 días, reiniciamos los días
                        if (streak.currentStreak == 7) {
                            resetStreakDays(userId)  // Reiniciar los días
                            _currentStreak.value = 0  // Reiniciar la racha directly on StateFlow - Correct
                        }

                        // Guardamos los cambios en Firestore
                        streakRepository.updateStreak(userId, streak)

                        // Actualiza el estado local en el ViewModel
                        _currentStreak.value = streak.currentStreak
                        _longestStreak.value = streak.longestStreak
                    }
                }
            } catch (e: Exception) {
                Log.e("StreakViewModel", "Error al actualizar la racha: ${e.message}")
            }
        }
    }


    // Llamar esta función cuando se reinicien los días
    private fun resetStreakDays(userId: String) {
        viewModelScope.launch {
            try {
                val resetResult = streakRepository.resetDays(userId)

                resetResult.onSuccess {
                    Log.d("StreakViewModel", "Días de la racha reiniciados correctamente")
                }.onFailure {
                    Log.e("StreakViewModel", "Error al reiniciar los días: ${it.message}")
                }
            } catch (e: Exception) {
                Log.e("StreakViewModel", "Error al reiniciar los días de la racha: ${e.message}")
            }
        }
    }

    // Llamar a esta función para añadir un nuevo día a la racha (cuando el usuario se conecta un nuevo día)
    private fun addDayToStreak(userId: String, day: Int, streakDay: StreakDay) {
        viewModelScope.launch {
            try {
                val addDayResult = streakRepository.addDayToStreak(userId, day, streakDay)

                addDayResult.onSuccess {
                    Log.d("StreakViewModel", "Día $day agregado correctamente")
                }.onFailure {
                    Log.e("StreakViewModel", "Error al agregar el día $day: ${it.message}")
                }
            } catch (e: Exception) {
                Log.e("StreakViewModel", "Error al agregar el día a la racha: ${e.message}")
            }
        }
    }
}
