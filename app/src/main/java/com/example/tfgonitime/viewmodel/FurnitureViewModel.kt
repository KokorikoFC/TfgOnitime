package com.example.tfgonitime.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgonitime.data.model.Furniture
import com.example.tfgonitime.data.repository.FurnitureRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class FurnitureUiState {
    object Loading : FurnitureUiState()
    data class Success(val furnitureByTheme: Map<String, List<Furniture>>) : FurnitureUiState()
    data class Error(val message: String) : FurnitureUiState()
}

class FurnitureViewModel(
    private val repository: FurnitureRepository = FurnitureRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<FurnitureUiState>(FurnitureUiState.Loading)
    val uiState: StateFlow<FurnitureUiState> = _uiState

    init {
        loadFurniture()
    }

    fun loadFurniture() {
        viewModelScope.launch {
            _uiState.value = FurnitureUiState.Loading
            try {
                // Obtenemos el resultado del repositorio
                val result = repository.getAllFurnitureGroupedByTheme()

                // Verificamos si la respuesta fue exitosa o no
                result.onSuccess { grouped ->

                    grouped.forEach { (theme, items) ->
                        Log.d("FurnitureViewModel", "Tema: $theme, Muebles: ${items.size}")
                        items.forEach { furniture ->
                            Log.d("FurnitureViewModel", "Mueble: ${furniture.name}, Precio: ${furniture.price}")
                        }
                    }

                    // Actualiza el estado con los muebles
                    _uiState.value = FurnitureUiState.Success(grouped)
                }.onFailure { e ->
                    _uiState.value = FurnitureUiState.Error("Error al cargar los muebles: ${e.message}")
                }
            } catch (e: Exception) {
                _uiState.value = FurnitureUiState.Error("Error al cargar los muebles: ${e.message}")
            }
        }
    }
}
