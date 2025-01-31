package com.example.tfgonitime.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tfgonitime.data.model.Streak
import com.google.firebase.firestore.FirebaseFirestore

class StreakViewModel() : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _streakLiveData = MutableLiveData<Streak?>()
    val streakLiveData: MutableLiveData<Streak?> get() = _streakLiveData

    private val _errorLiveData = MutableLiveData<String?>()
    val errorLiveData: MutableLiveData<String?> get() = _errorLiveData

    fun getStreak(userId: String) {
        val streakDocument = firestore.collection("streaks").document(userId)
        streakDocument.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val streak = document.toObject(Streak::class.java)
                    _streakLiveData.value = streak
                } else {
                    _errorLiveData.value = null
                }
            }
            .addOnFailureListener { exception ->
                _errorLiveData.value = "Error al obtener la racha: ${exception.message}"
            }
    }

}