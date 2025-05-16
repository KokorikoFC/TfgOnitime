package com.example.tfgonitime.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgonitime.data.model.Streak
import com.example.tfgonitime.data.repository.StreakRepository
import com.example.tfgonitime.data.repository.UserRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.ZoneId

class StreakViewModel : ViewModel() {

    private val streakRepository = StreakRepository()
    private val userRepository = UserRepository()

    private val userId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak

    private val _longestStreak = MutableStateFlow(0)
    val longestStreak: StateFlow<Int> = _longestStreak

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess

    private val _updateError = MutableStateFlow<String?>(null)
    val updateError: StateFlow<String?> = _updateError

    fun loadStreak(userId: String) {
        viewModelScope.launch {
            _loadingState.value = true
            val streakResult = streakRepository.getStreak(userId)

            streakResult.onSuccess { streak ->
                _loadingState.value = false
                if (streak != null) {
                    _currentStreak.value = streak.currentStreak
                    _longestStreak.value = streak.longestStreak
                }
            }.onFailure {
                _loadingState.value = false
                Log.e("StreakViewModel", "Error al cargar la racha: ${it.message}")
            }
        }
    }

    fun onOpenAppTodayClicked(userId: String) {
        viewModelScope.launch {
            _loadingState.value = true
            val result = updateStreakForToday(userId)
            _loadingState.value = false
            _updateSuccess.value = result

            if (result) {
                addCoinsToUser()
            }
        }
    }

    private suspend fun updateStreakForToday(userId: String): Boolean {
        try {
            Log.d("StreakViewModel", "updateStreakForToday - Start")

            val streakResult = streakRepository.getStreak(userId)

            return streakResult.fold(
                onSuccess = { streak ->
                    if (streak != null) {
                        val currentDate = getCurrentDate()
                        val lastCheckInDate = streak.lastCheckIn?.toDate()
                            ?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()

                        if (lastCheckInDate == null || isDateSkipped(lastCheckInDate, currentDate)) {
                            streak.currentStreak = 0
                            streak.longestStreak = 0
                        }

                        streak.currentStreak += 1

                        if (streak.currentStreak > streak.longestStreak) {
                            streak.longestStreak = streak.currentStreak
                        }

                        if (streak.currentStreak > 7) {
                            streak.currentStreak = 1
                            resetStreakDays(userId)
                        }

                        streak.lastCheckIn = Timestamp.now()

                        val updateResult = streakRepository.updateStreak(userId, streak)
                        if (updateResult.isSuccess) {
                            _currentStreak.value = streak.currentStreak
                            _longestStreak.value = streak.longestStreak
                            return true
                        } else {
                            Log.e("StreakViewModel", "Error al actualizar la racha")
                            _updateError.value = "No se pudo actualizar la racha"
                        }
                    }
                    return false
                },
                onFailure = {
                    Log.e("StreakViewModel", "Error al obtener la racha: ${it.message}")
                    _updateError.value = "Error al obtener la racha"
                    false
                }
            )
        } catch (e: Exception) {
            Log.e("StreakViewModel", "updateStreakForToday - Exception: ${e.message}")
            _updateError.value = "Error inesperado"
            return false
        }
    }

    private fun isDateSkipped(lastDate: LocalDate, currentDate: LocalDate): Boolean {
        return lastDate.until(currentDate).days > 1
    }

    private fun getCurrentDate(): LocalDate {
        return LocalDate.now(ZoneId.systemDefault())
    }

    private suspend fun resetStreakDays(userId: String) {
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

    fun clearUpdateState() {
        _updateSuccess.value = false
        _updateError.value = null
    }

    fun checkStreakAndNavigate(
        userId: String,
        onNavigate: (String) -> Unit
    ) {
        viewModelScope.launch {
            val showStreak = shouldShowStreakScreen(userId)
            val route = if (showStreak) "streakScreen" else "homeScreen"
            onNavigate(route)
        }
    }

    suspend fun shouldShowStreakScreen(userId: String): Boolean {
        val streakRepository = StreakRepository()
        val result = streakRepository.getStreak(userId)

        return result.fold(
            onSuccess = { streak ->
                val lastCheckIn = streak?.lastCheckIn?.toDate()
                    ?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
                val today = LocalDate.now()
                lastCheckIn != today
            },
            onFailure = {
                Log.e("Streak", "Error obteniendo streak: ${it.message}")
                true // En caso de error, mostramos la pantalla
            }
        )
    }

    fun addCoinsToUser() {
        val uid = userId
        if (uid == null) {
            Log.e("StreakViewModel", "No hay usuario logueado, no se pueden añadir monedas")
            return
        }

        viewModelScope.launch {
            val result = userRepository.addCoins(uid, 50)
            if (result.isSuccess) {
                Log.d("StreakViewModel", "Se añadieron 50 monedas al usuario $uid")
            } else {
                Log.e("StreakViewModel", "Error al añadir monedas al usuario $uid")
            }
        }
    }



}
