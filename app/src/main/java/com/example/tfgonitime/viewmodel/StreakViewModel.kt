package com.example.tfgonitime.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tfgonitime.data.model.Streak
import com.google.firebase.Timestamp
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
    fun updateStreak(userId: String) {
        val streakDocument = firestore.collection("streaks").document(userId)
        val currentDate = Timestamp.now()  // Corregido

        firestore.runTransaction { transaction ->  // Sintaxis corregida
            val snapshot = transaction.get(streakDocument)
            val streak = snapshot.toObject(Streak::class.java)

            if (streak != null) {
                val lastLoginDate = streak.lastLoginDate

                if (isNewDay(lastLoginDate)) {
                    streak.checkInCount += 1
                    streak.lastLoginDate = currentDate
                    streak.days[streak.checkInCount] = Streak.Day(
                        day = streak.checkInCount,
                        checkedToday = true,
                        reward = "100 coins"
                    )
                }

                transaction.set(streakDocument, streak)
            }
        }.addOnFailureListener { exception ->
            _errorLiveData.value = "Error al actualizar la racha: ${exception.message}"
        }
    }
    fun listenStreakChanges (userId: String) {
        val streakDocument = firestore.collection("streaks").document(userId)
        streakDocument.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                _errorLiveData.value = "Error al escuchar cambios en la racha: ${exception.message}"
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val streak = snapshot.toObject(Streak::class.java)
                _streakLiveData.value = streak
            }
        }
    }




}