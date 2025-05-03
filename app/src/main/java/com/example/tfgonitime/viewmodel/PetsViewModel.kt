package com.example.tfgonitime.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgonitime.data.model.Pets
import com.example.tfgonitime.data.repository.PetsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PetsViewModel() : ViewModel() { // Asegúrate de tener este constructor sin argumentos
    private val repository = PetsRepository()
    private val _pets = MutableStateFlow<List<Pets>>(emptyList())
    val pets: StateFlow<List<Pets>> = _pets
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        fetchPets()
    }

    private fun fetchPets() {
        viewModelScope.launch {
            try {
                val petsList = repository.getAllPets()
                _pets.value = petsList
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar las mascotas: ${e.message}"
            }
        }
    }
}