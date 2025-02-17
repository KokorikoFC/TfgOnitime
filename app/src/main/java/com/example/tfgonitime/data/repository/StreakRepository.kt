package com.example.tfgonitime.data.repository

import android.util.Log
import com.example.tfgonitime.data.model.Streak
import com.example.tfgonitime.data.model.StreakDay
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class StreakRepository {

    private val db = FirebaseFirestore.getInstance()

    // Función suspendida para obtener la racha de un usuario
    suspend fun getStreak(userId: String): Result<Streak?> {
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("streak")
                .document("diaryStreak")
                .get()
                .await()

            // Convertimos el snapshot a un objeto Streak
            val streak = snapshot.toObject(Streak::class.java)
            Result.success(streak)
        } catch (e: Exception) {
            Log.e("StreakRepository", "Error al obtener la racha: ${e.message}")
            Result.failure(e)
        }
    }

    // Función suspendida para actualizar la racha (cuando el usuario se conecta)
    suspend fun updateStreak(userId: String, streak: Streak): Result<Unit> {
        return try {
            val streakRef = db.collection("users")
                .document(userId)
                .collection("streak")
                .document("diaryStreak")

            // Guarda la racha actualizada
            streakRef.set(streak).await()

            // Usar directamente el valor de currentStreak sin sumar +1
            val currentDay = "day${streak.currentStreak}"
            val streakDayRef = streakRef.collection("days").document(currentDay)

            val streakDay = StreakDay(completed = true)
            streakDayRef.set(streakDay).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }




    // Función suspendida para inicializar la racha (si no existe)
    suspend fun initializeStreak(userId: String, streak: Streak): Result<Unit> {
        return try {
            db.collection("users")
                .document(userId)
                .collection("streak")
                .document("diaryStreak")
                .set(streak)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("StreakRepository", "Error al inicializar la racha: ${e.message}")
            Result.failure(e)
        }
    }

    // Función suspendida para restablecer los días de la racha (cuando la racha llega a 7 días)
    suspend fun resetDays(userId: String): Result<Unit> {
        return try {
            // Borra todos los documentos en la subcolección de días
            db.collection("users")
                .document(userId)
                .collection("streak")
                .document("diaryStreak")
                .collection("days")
                .get()
                .await()
                .documents
                .forEach { doc ->
                    doc.reference.delete().await()  // Elimina cada día
                }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("StreakRepository", "Error al reiniciar los días: ${e.message}")
            Result.failure(e)
        }
    }

    // Función suspendida para agregar un día a la racha (por ejemplo, el día 1, día 2, etc.)
    suspend fun addDayToStreak(userId: String, day: Int, streakDay: StreakDay): Result<Unit> {
        return try {
            val dayRef = db.collection("users")
                .document(userId)
                .collection("streak")
                .document("diaryStreak")
                .collection("days")
                .document("day$day")

            val snapshot = dayRef.get().await()

            if (!snapshot.exists()) {
                // Solo agregamos el día si no existe
                dayRef.set(streakDay).await()
                Result.success(Unit)
            } else {
                // El día ya existe, no lo añadimos
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Log.e("StreakRepository", "Error al agregar el día: ${e.message}")
            Result.failure(e)
        }
    }

}
