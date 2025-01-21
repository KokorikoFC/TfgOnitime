package com.example.tfgonitime.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.tfgonitime.R
import com.example.tfgonitime.data.model.Mood
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate

class DiaryViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    val moodEmojis = MutableStateFlow<Map<LocalDate, Int>>(emptyMap())
    val monthlyMoods = MutableStateFlow<List<Mood>>(emptyList())

    fun loadMoods(userId: String, year: String, month: String) {
        val userDocRef = firestore.collection("moods").document(userId)
        userDocRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val moods = mutableMapOf<LocalDate, Int>()
                val monthData = task.result?.get("moods.$year.$month") as? Map<*, *>
                monthData?.forEach { (date, mood) ->
                    val localDate = LocalDate.parse(date.toString())
                    val emojiResId = when (mood) {
                        "Bien" -> R.drawable.happy_face
                        "Bien Mal" -> R.drawable.happy_face
                        "Go go go" -> R.drawable.happy_face
                        else -> R.drawable.happy_face
                    }
                    moods[localDate] = emojiResId
                }
                moodEmojis.value = moods
            } else {
                Log.e("Firestore", "Error loading moods", task.exception)
            }
        }
    }


    fun loadMonthlyMoods(userId: String, year: String, month: String) {
        val userDocRef = firestore.collection("moods").document(userId)
        userDocRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val moodsList = mutableListOf<Mood>()
                val monthData = task.result?.get("moods.$year.$month") as? Map<*, *>
                monthData?.forEach { (date, moodData) ->
                    val moodMap = moodData as Map<*, *>
                    val mood = Mood(
                        userId = userId,
                        moodDate = moodMap["moodDate"] as String,
                        moodType = moodMap["moodType"] as String,
                        diaryEntry = moodMap["diaryEntry"] as String,
                        generatedLetter = moodMap["generatedLetter"] as String?
                    )
                    moodsList.add(mood)
                }
                monthlyMoods.value = moodsList
            } else {
                Log.e("Firestore", "Error loading monthly moods", task.exception)
            }
        }
    }
}


