package com.example.tfgonitime.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.tfgonitime.R
import com.example.tfgonitime.data.model.Mood
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate

class MoodViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    val moodEmojis = MutableStateFlow<Map<LocalDate, Int>>(emptyMap())

    fun saveUserMood(
        userId: String,
        moodDate: String,
        moodType: String,
        diaryEntry: String,
        motivationalMessage: String?,
    ) {
        val mood = Mood(
            userId = userId,
            moodDate = moodDate,
            moodType = moodType,
            diaryEntry = diaryEntry,
            generatedLetter = motivationalMessage
        )
        Log.d("SaveUserMood", "Mood created: $mood")
        saveMood(userId, mood)
    }

    private fun saveMood(userId: String, mood: Mood) {
        val userDocRef =
            firestore.collection("moods").document(userId)  // Documento por cada usuario

        userDocRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (!task.result.exists()) {
                    Log.d("Firestore", "User document does not exist, creating a new one.")
                    userDocRef.set(hashMapOf("userId" to userId))
                        .addOnSuccessListener {
                            Log.d("Firestore", "User document created successfully.")
                            saveMoodEntry(
                                userDocRef,
                                mood
                            ) // Si no existe el documento, se crea uno
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error creating user document", e)
                        }
                } else {
                    Log.d("Firestore", "User document exists.")
                    saveMoodEntry(
                        userDocRef,
                        mood
                    ) // Si ya existe el documento de usuario, simplemente agrega la entrada
                }
            } else {
                Log.e("Firestore", "Error checking user document", task.exception)
            }
        }
    }

    private fun saveMoodEntry(userDocRef: DocumentReference, mood: Mood) {
        val moodDate = LocalDate.parse(mood.moodDate)
        val year = moodDate.year.toString()
        val month = moodDate.monthValue.toString().padStart(2, '0')

        val moodMap = hashMapOf(
            "moodDate" to mood.moodDate,
            "moodType" to mood.moodType,
            "diaryEntry" to mood.diaryEntry,
            "generatedLetter" to mood.generatedLetter
        )

        userDocRef.update(
            "moods.$year.$month.${mood.moodDate}", moodMap
        ).addOnSuccessListener {
            Log.d(
                "Firestore",
                "Mood entry saved successfully in the map 'moods' under year and month."
            )
        }.addOnFailureListener { e ->
            Log.e(
                "Firestore",
                "Error saving mood entry in the map 'moods' under year and month.",
                e
            )
        }
    }

}





