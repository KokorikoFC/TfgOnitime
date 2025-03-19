package com.example.tfgonitime.data.repository

import android.util.Log
import com.example.tfgonitime.data.model.Mission
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MissionRepository {

    private val db = FirebaseFirestore.getInstance()

    // Función suspendida para obtener todas las misiones de un usuario
    suspend fun getMissions(userId: String): Result<List<Mission>> {
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("missions")
                .get()
                .await()

            Log.d("MissionRepository", "Misiones obtenidas: ${snapshot.documents.size}")

            val missions = snapshot.documents.mapNotNull { it.toObject(Mission::class.java) }
            Result.success(missions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Función suspendida para marcar una misión como completada
    suspend fun updateMissionCompletion(userId: String, missionId: String, isCompleted: Boolean): Result<Unit> {
        Log.d("MissionRepository", "Actualizando misión con userId: $userId y missionId: $missionId")

        return try {
            // Crear la referencia al documento de la misión específica
            val missionRef = db.collection("users")  // Colección de usuarios
                .document(userId)  // Documento de usuario
                .collection("missions")  // Subcolección 'missions' dentro del documento del usuario
                .document(missionId)  // Documento de la misión específico dentro de la subcolección

            // Actualizamos el campo 'isCompleted' de la misión
            missionRef.update("isCompleted", isCompleted).await()

            Result.success(Unit)
        } catch (e: Exception) {
            // Si ocurre un error, lo capturamos
            Log.e("MissionRepository", "Error al completar misión: ${e.message}")
            Result.failure(e)
        }
    }



    // Función suspendida para obtener una misión específica (si es necesario)
    suspend fun getMissionById(userId: String, missionId: String): Result<Mission?> {
        return try {
            val documentSnapshot = db.collection("users")
                .document(userId)
                .collection("missions")
                .document(missionId)
                .get()
                .await()

            val mission = documentSnapshot.toObject(Mission::class.java)
            Result.success(mission)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Función suspendida para agregar una nueva misión (si fuera necesario)
    suspend fun addMission(userId: String, mission: Mission): Result<String> {
        return try {
            val documentReference = db.collection("users")
                .document(userId)
                .collection("missions")
                .add(mission) // Usamos `add()` para que Firestore genere el ID automáticamente
                .await()

            val generatedId = documentReference.id
            val missionWithId = mission.copy(id = generatedId)

            documentReference.set(missionWithId).await()

            Result.success(generatedId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}