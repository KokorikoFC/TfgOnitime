package com.example.tfgonitime.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgonitime.data.model.Mission
import com.example.tfgonitime.data.repository.MissionRepository
import com.example.tfgonitime.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MissionViewModel : ViewModel() {

    private val missionRepository = MissionRepository()
    private val userRepository = UserRepository()

    private val _missionsState = MutableStateFlow<List<Mission>>(emptyList())
    val missionsState: StateFlow<List<Mission>> = _missionsState

    fun loadMissions(userId: String) {
        viewModelScope.launch {
            Log.d("MissionViewModel", "Cargando misiones para userId: $userId")
            val result = missionRepository.getMissions(userId)
            result.onSuccess { missions ->
                _missionsState.value = missions
                Log.d("MissionViewModel", "Misiones cargadas: ${missions.size}")
            }.onFailure { exception ->
                Log.e("MissionViewModel", "Error al obtener misiones: ${exception.message}")
            }
        }
    }

    fun completeMission(userId: String, missionId: String) {
        viewModelScope.launch {
            val result = missionRepository.updateMissionCompletion(userId, missionId, true)
            result.onSuccess {
                // Actualizar estado local
                _missionsState.value = _missionsState.value.map {
                    if (it.id == missionId) {
                        it.copy(isCompleted = true)
                    } else {
                        it
                    }
                }
                Log.d("MissionViewModel", "Misión $missionId completada correctamente")
            }.onFailure { exception ->
                Log.e("MissionViewModel", "Error al completar misión: ${exception.message}")
            }
        }
    }

    fun checkMissionProgress(userId: String) {
        viewModelScope.launch {
            val result =
                userRepository.getUserTasksCompleted(userId) // Usa la función de UserRepository

            result.onSuccess { tasksCompleted ->
                Log.d(
                    "MissionViewModel",
                    "Verificando misiones con $tasksCompleted tareas completadas."
                )

                _missionsState.value.forEach { mission ->
                    if (!mission.isCompleted) {
                        when (mission.triggerAction) {
                            "complete_first_task" -> {
                                if (tasksCompleted >= 1) {
                                    completeMission(userId, mission.id)
                                }
                            }

                            "complete_five_tasks" -> {
                                if (tasksCompleted >= 5) {
                                    completeMission(userId, mission.id)
                                }
                            }
                            // ... (otros casos)
                        }
                    }
                }
            }.onFailure { exception ->
                Log.e("MissionViewModel", "Error al verificar misiones: ${exception.message}")
            }
        }
    }
}