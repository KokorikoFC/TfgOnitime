package com.example.tfgonitime.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.time.LocalDate

class DiaryViewModel : ViewModel() {

    // Mapa para almacenar el estado de ánimo por fecha (con ID de imagen)
    private val _moodEmojis = mutableStateOf<Map<LocalDate, Int>>(emptyMap())
    val moodEmojis: State<Map<LocalDate, Int>> = _moodEmojis

    // Función para guardar el estado de ánimo (emoji) de un día
    fun setMoodEmoji(date: LocalDate, emojiResId: Int) {
        _moodEmojis.value = _moodEmojis.value + (date to emojiResId)
    }

}