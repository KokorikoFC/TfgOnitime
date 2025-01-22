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

    private val _isMoodRegisteredToday = MutableStateFlow(false)
    val isMoodRegisteredToday: StateFlow<Boolean> = _isMoodRegisteredToday

    private val _selectedMood = MutableStateFlow<Mood?>(null)
    val selectedMood: StateFlow<Mood?> = _selectedMood

    fun addMood(userId: String, mood: Mood) {
        viewModelScope.launch {
            _loadingState.value = true
            val result = diaryRepository.addMood(userId, mood)
            _loadingState.value = false

            result.onSuccess {
                println("Mood agregado")
                checkMoodRegisteredToday(userId)
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
                checkMoodRegisteredToday(userId)
                println("Moods cargados: $moods")
            }.onFailure {
                println("Error al obtener moods: ${it.message}")
            }
        }
    }

    /*
    fun updateMood(userId: String, mood: Mood) {
        viewModelScope.launch {
            _loadingState.value = true
            val result = diaryRepository.updateMood(userId, mood)
            _loadingState.value = false

            result.onSuccess {
                println("Mood actualizado")
                checkMoodRegisteredToday(userId)
                loadMoods(userId, mood.moodDate.substring(0, 4), mood.moodDate.substring(5, 7))
            }.onFailure {
                println("Error al actualizar mood: ${it.message}")
            }
        }
    }

    fun deleteMood(userId: String, moodDate: String) {
        viewModelScope.launch {
            _loadingState.value = true
            val result = diaryRepository.deleteMood(userId, moodDate)
            _loadingState.value = false

            result.onSuccess {
                println("Mood eliminado")
                checkMoodRegisteredToday(userId)
                loadMoods(userId, moodDate.substring(0, 4), moodDate.substring(5, 7))
            }.onFailure {
                println("Error al eliminar mood: ${it.message}")
            }
        }
    }
    */

    fun updateMoodEmojis(moods: List<Mood>) {
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

    fun checkMoodRegisteredToday(userId: String) {
        viewModelScope.launch {
            val today = LocalDate.now().toString()
            val result =
                diaryRepository.getMoods(userId, today.substring(0, 4), today.substring(5, 7))
            val isRegistered = result.getOrDefault(emptyList()).any { it.moodDate == today }
            _isMoodRegisteredToday.value = isRegistered
        }
    }

    fun checkMoodRegistered(userId: String, date: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = diaryRepository.getMoods(userId, date.substring(0, 4), date.substring(5, 7))
            val mood = result.getOrDefault(emptyList()).find { it.moodDate == date }
            _selectedMood.value = mood
            onResult(mood != null)
        }
    }

}





