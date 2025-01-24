package com.example.tfgonitime.data.repository

import com.example.tfgonitime.data.model.Mood
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DiaryRepository {

    private val db = FirebaseFirestore.getInstance()

    // Función suspendida para agregar un mood
    suspend fun addMood(userId: String, mood: Mood): Result<Unit> {
        return try {
            db.collection("users")
                .document(userId)
                .collection("moods")
                .document(mood.moodDate) // Usar moodDate como ID del documento
                .set(mood)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Función suspendida para obtener todos los moods de un usuario por mes y año
    suspend fun getMoods(userId: String, year: String, month: String): Result<List<Mood>> {
        return try {
            val firstDayOfMonth = "$year-$month-01"
            val lastDayOfMonth = "$year-$month-31"

            val snapshot = db.collection("users")
                .document(userId)
                .collection("moods")
                .whereGreaterThanOrEqualTo("moodDate", firstDayOfMonth)
                .whereLessThanOrEqualTo("moodDate", lastDayOfMonth)
                .get()
                .await()

            val moods = snapshot.documents.mapNotNull { it.toObject(Mood::class.java) }
            Result.success(moods)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMoodById(userId: String, moodDate: String): Result<Mood?> {
        return try {
            // Obtener el documento correspondiente a moodDate
            val document = db.collection("users")
                .document(userId)
                .collection("moods")
                .document(moodDate)
                .get()
                .await()


            // Convertir el documento a un objeto Mood (asegúrate de que tu clase Mood esté preparada para este mapeo)
            val mood = document.toObject(Mood::class.java)
            Result.success(mood)
        } catch (e: Exception) {
            Result.failure(e) // Manejo de errores
        }
    }

    // Función suspendida para actualizar un mood
    suspend fun updateMood(userId: String, moodId: String, updatedMood: Mood): Result<Unit> {
        return try {
            val dateParts = updatedMood.moodDate.split("-")
            val year = dateParts[0]
            val month = dateParts[1]
            val day = dateParts[2]

            db.collection("users")
                .document(userId)
                .collection("moods")
                .document(moodId)
                .update(
                    mapOf(
                        "moods.$year.$month.$day" to updatedMood
                    )
                )
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Función suspendida para eliminar un mood
    suspend fun deleteMood(userId: String, moodId: String, moodDate: String): Result<Unit> {
        return try {
            val dateParts = moodDate.split("-")
            val year = dateParts[0]
            val month = dateParts[1]
            val day = dateParts[2]

            db.collection("users")
                .document(userId)
                .collection("moods")
                .document(moodId)
                .update(
                    mapOf(
                        "moods.$year.$month.$day" to FieldValue.delete()
                    )
                )
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}





