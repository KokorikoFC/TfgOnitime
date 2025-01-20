package com.example.tfgonitime.data.repository

import com.example.tfgonitime.data.model.Mood
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DiaryRepository {

    private val db = FirebaseFirestore.getInstance()

    // Función suspendida para agregar un mood
    suspend fun addMood(userId: String, mood: Mood): Result<String> {
        return try {
            val dateParts = mood.moodDate.split("-")
            val year = dateParts[0]
            val month = dateParts[1]
            val day = dateParts[2]

            val documentReference = db.collection("users")
                .document(userId)
                .collection("moods")
                .add(mapOf(
                    "moods" to mapOf(
                        year to mapOf(
                            month to mapOf(
                                day to mood
                            )
                        )
                    )
                ))
                .await()

            Result.success(documentReference.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Función suspendida para obtener todos los moods de un usuario por mes y año
    suspend fun getMoods(userId: String, year: String, month: String): Result<List<Mood>> {
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("moods")
                .get()
                .await()

            val moods = mutableListOf<Mood>()
            for (document in snapshot.documents) {
                val moodsMap = document.get("moods") as? Map<*, *>
                val yearMap = moodsMap?.get(year) as? Map<*, *>
                val monthMap = yearMap?.get(month) as? Map<*, *>
                monthMap?.forEach { (day, moodData) ->
                    val mood = (moodData as? Map<*, *>)?.let {
                        Mood(
                            id = document.id,
                            moodDate = "$year-$month-$day",
                            moodType = it["moodType"].toString(),
                            diaryEntry = it["diaryEntry"].toString(),
                            generatedLetter = it["generatedLetter"]?.toString()
                        )
                    }
                    if (mood != null) {
                        moods.add(mood)
                    }
                }
            }
            Result.success(moods)
        } catch (e: Exception) {
            Result.failure(e)
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
                .update(mapOf(
                    "moods.$year.$month.$day" to updatedMood
                ))
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
                .update(mapOf(
                    "moods.$year.$month.$day" to FieldValue.delete()
                ))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}





