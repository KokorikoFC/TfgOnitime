package com.example.tfgonitime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgonitime.R
import com.example.tfgonitime.data.model.Mood
import com.example.tfgonitime.data.repository.ChatRepository
import com.example.tfgonitime.data.repository.DiaryRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class DiaryViewModel : ViewModel() {

    private val diaryRepository = DiaryRepository()
    private val chatRepository = ChatRepository()

    private val _moodsState = MutableStateFlow<List<Mood>>(emptyList())
    val moodsState: StateFlow<List<Mood>> = _moodsState

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    val moodEmojis = MutableStateFlow<Map<LocalDate, Int>>(emptyMap())

    private val _isMoodRegisteredToday = MutableStateFlow(false)
    val isMoodRegisteredToday: StateFlow<Boolean> = _isMoodRegisteredToday

    private val _selectedMood = MutableStateFlow<Mood?>(null)
    val selectedMood: StateFlow<Mood?> = _selectedMood

    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    fun addMood(
        userId: String,
        mood: Mood,
        onSuccess: () -> Unit,  // <-- Agregamos este parámetro
        onError: (String) -> Unit
    ) {
        // Validar campos vacíos
        if (mood.moodType.isEmpty()) {
            println("El campo moodType está vacío")
            onError("Seleccione un estado de ánimo")
            return
        }

        if (mood.diaryEntry.isEmpty()) {
            println("El campo diaryEntry está vacío")
            onError("Es necesario escribir algo en el diario")
            return
        }

        viewModelScope.launch {
            _loadingState.value = true
            val result = diaryRepository.addMood(userId, mood)
            _loadingState.value = false

            result.onSuccess {
                println("Mood agregado")
                checkMoodRegisteredToday(userId)
                loadMoods(userId, mood.moodDate.substring(0, 4), mood.moodDate.substring(5, 7))
                onSuccess()  // <-- Llamamos a onSuccess cuando se agrega correctamente
            }.onFailure {
                println("Error al agregar mood: ${it.message}")
                onError(it.message ?: "Error desconocido")  // <-- Pasamos el error
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

    fun getMoodById(moodDate: String) {

        println("Mood obtenido: $moodDate")

        viewModelScope.launch {
            _loadingState.value = true
            val result = diaryRepository.getMoodById(userId, moodDate)
            _loadingState.value = false

            result.onSuccess { mood ->
                _selectedMood.value = mood
                println("Mood obtenido: $mood")
            }.onFailure {
                println("Error al obtener mood: ${it.message}")
            }
        }
    }

    // Método para borrar un mood
    fun deleteMood( moodDate: String) {
        viewModelScope.launch {
            _loadingState.value = true

            val result = diaryRepository.deleteMood(userId, moodDate)
            _loadingState.value = false

            result.onSuccess {
                println("Mood borrado exitosamente")
                loadMoods(userId, moodDate.substring(0, 4), moodDate.substring(5, 7))
            }.onFailure {
                println("Error al borrar el mood: ${it.message}")
            }
        }
    }

    // Método para actualizar un mood
    fun updateMood(
        mood: Mood,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        // Validar campos vacíos
        if (mood.moodType.isEmpty()) {
            println("El campo moodType está vacío")
            onError("Seleccione un estado de ánimo")
            return
        }

        if (mood.diaryEntry.isEmpty()) {
            println("El campo diaryEntry está vacío")
            onError("Es necesario escribir algo en el diario")
            return
        }

        viewModelScope.launch {
            _loadingState.value = true

            val result = diaryRepository.updateMood(userId, mood)
            _loadingState.value = false

            result.onSuccess {
                println("Mood actualizado exitosamente")
                onSuccess() // Llamar a onSuccess cuando la actualización sea exitosa
            }.onFailure {
                println("Error al actualizar el mood: ${it.message}")
                onError(it.message ?: "Error desconocido") // Llamar a onError con el mensaje de error
            }
        }
    }


    fun updateMoodEmojis(moods: List<Mood>) {
        val emojis = mutableMapOf<LocalDate, Int>()
        moods.forEach { mood ->
            val localDate = LocalDate.parse(mood.moodDate)
            val emojiResId = when (mood.moodType) {
                "fantastico" -> R.drawable.emotionface_veryhappy
                "feliz" -> R.drawable.emotionface_happy
                "masomenos" -> R.drawable.emotionface_neutral
                "triste" -> R.drawable.emotionface_sad
                "deprimido" -> R.drawable.emotionface_verysad
                else -> R.drawable.emotionface_happy
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
            val result =
                diaryRepository.getMoods(userId, date.substring(0, 4), date.substring(5, 7))
            val mood = result.getOrDefault(emptyList()).find { it.moodDate == date }
            _selectedMood.value = mood
            onResult(mood != null)
        }
    }

    fun clearSelectedMood() {
        _selectedMood.value = null
    }

}
