package com.example.tfgonitime.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgonitime.R
import com.example.tfgonitime.data.model.Mood
import com.example.tfgonitime.data.repository.DiaryRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class DiaryViewModel : ViewModel() {

    private val diaryRepository = DiaryRepository()

    private val _moodsState = MutableStateFlow<List<Mood>>(emptyList())
    val moodsState: StateFlow<List<Mood>> = _moodsState

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    val moodEmojis = MutableStateFlow<Map<LocalDate, Int>>(emptyMap())

    fun addMood(userId: String, mood: Mood) {
        viewModelScope.launch {
            _loadingState.value = true
            val result = diaryRepository.addMood(userId, mood)
            _loadingState.value = false

            result.onSuccess { moodId ->
                println("Mood agregado con ID: $moodId")
                loadMoods(userId, mood.moodDate.substring(0, 4), mood.moodDate.substring(5, 7))
            }.onFailure {
                println("Error al agregar mood: ${it.message}")
            }
        }
    }

    fun loadMoods(userId: String, year: String, month: String) {
        viewModelScope.launch {
            _loadingState.value = true
            val result = diaryRepository.getMoods(userId, year, month)
            _loadingState.value = false

            result.onSuccess { moods ->
                _moodsState.value = moods
                updateMoodEmojis(moods)
                println("Moods cargados: $moods")
            }.onFailure {
                println("Error al obtener moods: ${it.message}")
            }
        }
    }

    fun updateMood(userId: String, moodId: String, updatedMood: Mood) {
        viewModelScope.launch {
            _loadingState.value = true
            val result = diaryRepository.updateMood(userId, moodId, updatedMood)
            _loadingState.value = false

            result.onSuccess {
                println("Mood actualizado")
                loadMoods(userId, updatedMood.moodDate.substring(0, 4), updatedMood.moodDate.substring(5, 7))
            }.onFailure {
                println("Error al actualizar mood: ${it.message}")
            }
        }
    }

    fun deleteMood(userId: String, moodId: String, moodDate: String) {
        viewModelScope.launch {
            _loadingState.value = true
            val result = diaryRepository.deleteMood(userId, moodId, moodDate)
            _loadingState.value = false

            result.onSuccess {
                println("Mood eliminado")
                loadMoods(userId, moodDate.substring(0, 4), moodDate.substring(5, 7))
            }.onFailure {
                println("Error al eliminar mood: ${it.message}")
            }
        }
    }

    private fun updateMoodEmojis(moods: List<Mood>) {
        val emojis = mutableMapOf<LocalDate, Int>()
        moods.forEach { mood ->
            val localDate = LocalDate.parse(mood.moodDate)
            val emojiResId = when (mood.moodType) {
                "Bien" -> R.drawable.happy_face
                "Bien Mal" -> R.drawable.happy_face
                "Go go go" -> R.drawable.happy_face
                else -> R.drawable.happy_face
            }
            emojis[localDate] = emojiResId
        }
        moodEmojis.value = emojis
    }
}



