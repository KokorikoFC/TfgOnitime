package com.example.tfgonitime.data.repository

import android.util.Log
import com.example.tfgonitime.data.model.Streak
import com.example.tfgonitime.data.model.StreakDay
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

class StreakRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getStreak(userId: String): Result<Streak?> { // Ahora devuelve Streak?
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("streak")
                .document("diaryStreak")
                .get()
                .await()

            val streak = snapshot.toObject(Streak::class.java)
            Result.success(streak) // Puede ser null si no existe
        } catch (e: Exception) {
            Log.e("StreakRepository", "Error al obtener la racha", e)
            Result.failure(e)
        }
    }

    suspend fun getStreakDay(userId: String, dayOfWeek: DayOfWeek): Result<StreakDay?> { // Obtener datos de un día específico
        return try {
            val dayOfWeekString = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()).lowercase() // Convertir DayOfWeek a String (ej: "monday")

            val snapshot = db.collection("users")
                .document(userId)
                .collection("streak")
                .document("diaryStreak")
                .collection("days")
                .document(dayOfWeekString) // Usar el nombre del día como ID del documento
                .get()
                .await()

            val streakDay = snapshot.toObject(StreakDay::class.java)
            Result.success(streakDay) // Puede ser null si no existe
        } catch (e: Exception) {
            Log.e("StreakRepository", "Error al obtener los datos del día", e)
            Result.failure(e)
        }
    }


    suspend fun markDayCompleted(userId: String, dayOfWeek: DayOfWeek, petId: String?, reward: String?): Result<Unit> { // Permite petId y reward nulos
        return try {
            val dayOfWeekString = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()).lowercase() // Convertir DayOfWeek a String

            // Obtener el documento del día en "days" o crearlo si no existe
            val dayRef = db.collection("users")
                .document(userId)
                .collection("streak")
                .document("diaryStreak")
                .collection("days")
                .document(dayOfWeekString)


            // Crear un nuevo objeto StreakDay con los datos
            val streakDay = StreakDay(completed = true, petId = petId, reward = reward)

            // Guardar (o sobrescribir si ya existe) el documento del día
            dayRef.set(streakDay).await()


            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("StreakRepository", "Error al marcar el día como completado", e)
            Result.failure(e)
        }
    }

    suspend fun updateStreak(userId: String, streak: Streak): Result<Unit> { // Función para actualizar la racha general
        return try {
            db.collection("users")
                .document(userId)
                .collection("streak")
                .document("diaryStreak")
                .set(streak) // Guarda el objeto Streak completo
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("StreakRepository", "Error al actualizar la racha", e)
            Result.failure(e)
        }
    }


    suspend fun initializeStreak(userId: String, streak: Streak): Result<Unit> { // Función para inicializar la racha general
        return try {
            db.collection("users")
                .document(userId)
                .collection("streak")
                .document("diaryStreak")
                .set(streak)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("StreakRepository", "Error al inicializar la racha", e)
            Result.failure(e)
        }
    }
}