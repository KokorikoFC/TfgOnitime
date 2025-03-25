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
                Log.d("MissionViewModel", "Misiones cargadas: ${missions.size}")

                missions.forEach {
                    Log.d("MissionViewModel", "Misión: id=${it.id}, isCompleted=${it.isCompleted}, isClaimed=${it.isClaimed}, monedas=${it.reward}")
                }

                _missionsState.value = missions
            }.onFailure { exception ->
                Log.e("MissionViewModel", "Error al obtener misiones: ${exception.message}")
            }
        }
    }


    fun checkMissionProgress(userId: String) {
        viewModelScope.launch {
            val result = userRepository.getUserTasksCompleted(userId) // Usa la función de UserRepository

            result.onSuccess { tasksCompleted ->
                Log.d(
                    "MissionViewModel",
                    "Verificando misiones con $tasksCompleted tareas completadas."
                )

                // Create a mutable copy of the current missions state
                val updatedMissions = _missionsState.value.toMutableList()

                updatedMissions.forEachIndexed { index, mission ->
                    if (!mission.isCompleted) {  // Solo verificamos misiones que no están completadas
                        when (mission.triggerAction) {
                            "complete_first_task" -> {
                                if (tasksCompleted >= 1) {
                                    // Aquí aseguramos que la misión se complete correctamente
                                    completeMissionAndUpdateState(userId, mission.id, updatedMissions, index)
                                }
                            }

                            "complete_five_tasks" -> {
                                if (tasksCompleted >= 5) {
                                    // Aquí aseguramos que la misión se complete correctamente
                                    completeMissionAndUpdateState(userId, mission.id, updatedMissions, index)
                                }
                            }
                            // Agrega más condiciones si es necesario
                        }
                    }
                }
                // Update the missions state with the modified list
                _missionsState.value = updatedMissions
            }.onFailure { exception ->
                Log.e("MissionViewModel", "Error al verificar misiones: ${exception.message}")
            }
        }
    }

    // Helper function to complete the mission and update the local state
    private suspend fun completeMissionAndUpdateState(userId: String, missionId: String, missions: MutableList<Mission>, index: Int) {
        try {
            val result = missionRepository.updateMissionCompletion(userId, missionId, true)
            result.onSuccess {
                Log.d("MissionViewModel", "Misión $missionId completada con éxito")
                // Update the local state
                missions[index] = missions[index].copy(isCompleted = true)
            }.onFailure {
                Log.e("MissionViewModel", "Error al completar la misión: ${it.message}")
            }
        } catch (e: Exception) {
            Log.e("MissionViewModel", "Error al completar la misión: ${e.message}")
        }
    }


    fun claimMissionReward(userId: String, missionId: String) {
        viewModelScope.launch {
            // Asegurarnos de que la misión esté completada antes de reclamarla
            val mission = _missionsState.value.find { it.id == missionId }
            if (mission?.isCompleted == true) {
                try {
                    val result = missionRepository.claimMissionReward(userId, missionId)
                    if (result.isSuccess) {
                        // Actualizar el estado local de la misión
                        _missionsState.value = _missionsState.value.map {
                            if (it.id == missionId) it.copy(isClaimed = true) else it
                        }
                        Log.d("MissionViewModel", "Recompensa de misión reclamada: $missionId")

                        // Sumar la recompensa a las monedas del usuario
                        mission.reward.let { rewardAmount ->
                            if (rewardAmount != null && rewardAmount > 0) {
                                viewModelScope.launch {
                                    val addCoinsResult = userRepository.addCoins(userId, rewardAmount)
                                    if (addCoinsResult.isSuccess) {
                                        Log.d("MissionViewModel", "Se añadieron $rewardAmount monedas al usuario.")
                                    } else {
                                        Log.e("MissionViewModel", "Error al añadir monedas al usuario.")
                                    }
                                }
                            }
                        }

                    } else {
                        Log.e("MissionViewModel", "Error al reclamar recompensa")
                    }
                } catch (e: Exception) {
                    Log.e("MissionViewModel", "Error al reclamar recompensa: ${e.message}")
                }
            } else {
                Log.d("MissionViewModel", "La misión no está completada. No se puede reclamar la recompensa.")
            }
        }
    }

    // New function to trigger mission completion check for a specific mission (optional, can be simplified)
    fun triggerMissionCompletionCheck(userId: String) {
        checkMissionProgress(userId)
    }
}