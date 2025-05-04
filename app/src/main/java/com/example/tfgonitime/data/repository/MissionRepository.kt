package com.example.tfgonitime.data.repository

import android.util.Log
import com.example.tfgonitime.data.model.Mission
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MissionRepository {

    private val db = FirebaseFirestore.getInstance()

    // Función para obtener todas las misiones de un usuario
    suspend fun getMissions(userId: String): Result<List<Mission>> {
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("missions")
                .get()
                .await()

            val missions = snapshot.documents.mapNotNull { doc ->
                val id = doc.getString("id") ?: ""
                val description = doc.getString("description") ?: ""
                val reward = doc.get("reward")?.let {
                    when (it) {
                        is Long -> it.toInt()
                        is Double -> it.toInt()
                        else -> 0
                    }
                } ?: 0

                val isCompleted = doc.getBoolean("isCompleted") ?: false
                val isClaimed = doc.getBoolean("isClaimed") ?: false
                val triggerAction = doc.getString("triggerAction") ?: ""

                Mission(id, description, isCompleted, isClaimed, triggerAction, "", reward)
            }

            Result.success(missions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Asignar misiones iniciales al nuevo usuario
    suspend fun assignInitialMissions(userId: String): Result<Unit> {
        return try {
            val initialMissions = listOf(
                Mission(
                    id = "mission1",
                    description = "Completa tu primera tarea",
                    isCompleted = false,
                    isClaimed = false,
                    triggerAction = "complete_first_task",
                    reward = 10
                ),
                Mission(
                    id = "mission2",
                    description = "Completa cinco tarea",
                    isCompleted = false,
                    isClaimed = false,
                    triggerAction = "complete_five_tasks",
                    reward = 20
                ),
                Mission(
                    id = "mission3",
                    description = "Completa diez tareas",
                    isCompleted = false,
                    isClaimed = false,
                    triggerAction = "complete_ten_tasks",
                    reward = 30
                ),
                Mission(
                    id = "mission4",
                    description = "Completa cincuenta tareas",
                    isCompleted = false,
                    isClaimed = false,
                    triggerAction = "complete_fifteen_tasks",
                    reward = 30
                )
            )

            val userMissionsRef = db.collection("users").document(userId).collection("missions")

            // Asignar cada misión inicial al usuario
            for (mission in initialMissions) {
                userMissionsRef.document(mission.id).set(mission).await()
            }

            // Retornar éxito
            Result.success(Unit)
        } catch (e: Exception) {
            // En caso de error, retornar el error
            Result.failure(e)
        }
    }


    // Función para completar la misión
    suspend fun updateMissionCompletion(
        userId: String,
        missionId: String,
        isCompleted: Boolean
    ): Result<Unit> {
        return try {
            val missionRef = db.collection("users")
                .document(userId)
                .collection("missions")
                .document(missionId)

            missionRef.update("isCompleted", isCompleted).await()
            Log.d("MissionRepository", "Misión $missionId completada: $isCompleted")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MissionRepository", "Error al actualizar la misión: ${e.message}")
            Result.failure(e)
        }
    }


    // Función para reclamar la recompensa de la misión
    suspend fun claimMissionReward(userId: String, missionId: String): Result<Unit> {
        return try {
            val missionRef = db.collection("users")
                .document(userId)
                .collection("missions")
                .document(missionId)

            missionRef.update("isClaimed", true).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
