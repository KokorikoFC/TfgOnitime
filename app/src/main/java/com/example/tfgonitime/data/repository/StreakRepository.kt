package com.example.tfgonitime.data.repository

import android.util.Log
import com.example.tfgonitime.data.model.Streak
import com.example.tfgonitime.data.model.StreakDay
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class StreakRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getStreak(userId: String): Result<Streak?> {
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("streak")
                .document("diaryStreak")
                .get()
                .await()

            val streak = snapshot.toObject(Streak::class.java)
            Result.success(streak)
        } catch (e: Exception) {
            Log.e("StreakRepository", "Error al obtener la racha: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun updateStreak(userId: String, streak: Streak): Result<Unit> {
        return try {
            val streakRef = db.collection("users")
                .document(userId)
                .collection("streak")
                .document("diaryStreak")

            val snapshot = streakRef.get().await()
            val streak = snapshot.toObject(Streak::class.java)

            if (streak == null) {
                return Result.failure(Exception("No se encontró la racha para el usuario"))
            }

            val lastCheckIn = streak.lastCheckIn?.toDate()?.toInstant()?.atZone(java.time.ZoneId.systemDefault())?.toLocalDate()
            val today = java.time.LocalDate.now()

            if (lastCheckIn == today) {
                return Result.failure(Exception("La racha ya fue actualizada hoy"))
            }

            val newStreak = if (lastCheckIn != null && lastCheckIn.plusDays(1) == today) {
                streak.currentStreak + 1  // Se mantiene la racha
            } else {
                1  // Se reinicia la racha porque se saltó un día
            }

            val updatedStreak = streak.copy(
                currentStreak = newStreak,
                lastCheckIn = com.google.firebase.Timestamp.now()
            )

            // Actualizar el documento de la racha
            streakRef.set(updatedStreak).await()

            // Registrar el nuevo día en la subcolección "days"
            val dayRef = streakRef.collection("days").document("day$newStreak")
            val streakDay = StreakDay(completed = true)
            dayRef.set(streakDay).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("StreakRepository", "Error al actualizar la racha: ${e.message}")
            Result.failure(e)
        }
    }


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

    suspend fun resetDays(userId: String): Result<Unit> {
        return try {
            db.collection("users")
                .document(userId)
                .collection("streak")
                .document("diaryStreak")
                .collection("days")
                .get()
                .await()
                .documents
                .forEach { doc ->
                    doc.reference.delete().await()
                }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("StreakRepository", "Error al reiniciar los días: ${e.message}")
            Result.failure(e)
        }
    }

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
                dayRef.set(streakDay).await()
                Result.success(Unit)
            } else {
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Log.e("StreakRepository", "Error al agregar el día: ${e.message}")
            Result.failure(e)
        }
    }
}
