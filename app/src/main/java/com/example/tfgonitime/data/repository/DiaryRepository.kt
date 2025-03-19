package com.example.tfgonitime.data.repository

import android.icu.text.SimpleDateFormat
import com.example.tfgonitime.data.model.Mood
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.Locale

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

    suspend fun getMoodOfTheDay(userId: String): Result<Mood> {
        return try {
            // Obtener la fecha actual en formato yyyy-MM-dd
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            // Acceder al documento correspondiente a hoy
            val document = db.collection("users")
                .document(userId)
                .collection("moods")
                .document(today)
                .get()
                .await()

            // Obtener los campos "moodDate", "moodType", "diaryEntry", "generatedLetter"
            val moodType = document.getString("moodType")
            val diaryEntry = document.getString("diaryEntry")
            val generatedLetter = document.getString("generatedLetter")

            if (moodType != null && diaryEntry != null) {
                // Rellenar los campos de Mood
                val mood = Mood(
                    id = document.id, // Se asigna el ID del documento como ID de Mood
                    moodDate = today,
                    moodType = moodType,
                    diaryEntry = diaryEntry,
                    generatedLetter = generatedLetter
                )

                Result.success(mood) // Devolver el estado de ánimo y la entrada del diario
            } else {
                Result.failure(Exception("No se encontraron los datos del día"))
            }
        } catch (e: Exception) {
            Result.failure(e) // Manejo de errores
        }
    }

    // Método suspendido para actualizar un mood
    suspend fun updateMood(userId: String, mood: Mood): Result<Unit> {
        return try {
            db.collection("users")
                .document(userId)
                .collection("moods")
                .document(mood.moodDate)
                .set(mood) // Usar `.set` para sobrescribir el documento existente
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Función suspendida para eliminar un mood
    suspend fun deleteMood(userId: String, moodDate: String): Result<Unit> {
        return try {
            db.collection("users")
                .document(userId)
                .collection("moods")
                .document(moodDate)
                .delete()
                .await() // Eliminar el documento completo

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}





