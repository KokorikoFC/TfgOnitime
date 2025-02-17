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

    private val streakRepository = StreakRepository()  // Inicialización del repositorio

    // Estado para la racha actual
    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak

    // Estado para la racha más larga
    private val _longestStreak = MutableStateFlow(0)
    val longestStreak: StateFlow<Int> = _longestStreak

    // Estado de carga (loading)
    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    // Función para cargar la racha del usuario
    fun loadStreak(userId: String) {
        viewModelScope.launch {
            _loadingState.value = true
            // Llama al repositorio para obtener la racha
            val streakResult = streakRepository.getStreak(userId)

            streakResult.onSuccess { streak ->
                _loadingState.value = false
                if (streak != null) {
                    _currentStreak.value = streak.currentStreak
                    _longestStreak.value = streak.longestStreak
                }
            }.onFailure {
                _loadingState.value = false
                // Manejar el error
            }
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
                            streak.longestStreak = streak.currentStreak  // Actualizamos la racha más larga
                        }

                        // Si llega a 7 días, reiniciamos los días
                        if (streak.currentStreak == 7) {
                            resetStreakDays(userId)  // Reiniciar los días
                            streak.currentStreak = 0  // Reiniciar la racha
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
    private fun addDayToStreak(userId: String, day: Int) {
        viewModelScope.launch {
            try {
                // Crear el objeto StreakDay (por ejemplo, indicando que el día está completado)
                val streakDay = StreakDay(completed = true)

                // Agregar el día correspondiente a la subcolección
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
