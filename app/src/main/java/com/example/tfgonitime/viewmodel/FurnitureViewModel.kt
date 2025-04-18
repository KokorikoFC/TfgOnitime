package com.example.tfgonitime.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgonitime.data.model.Furniture
import com.example.tfgonitime.data.repository.FurnitureRepository
import com.example.tfgonitime.data.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class FurnitureUiState {
    object Loading : FurnitureUiState()
    data class Success(val furnitureByTheme: Map<String, List<Furniture>>) : FurnitureUiState()
    data class Error(val message: String) : FurnitureUiState()
}

class FurnitureViewModel : ViewModel() {
    private val repository: FurnitureRepository = FurnitureRepository()
    private val db = FirebaseFirestore.getInstance()
    private val userRepository = UserRepository()

    private val _uiState = MutableStateFlow<FurnitureUiState>(FurnitureUiState.Loading)
    val uiState: StateFlow<FurnitureUiState> = _uiState

    private val _coins = MutableStateFlow(0)
    val coins: StateFlow<Int> = _coins

    init {
        loadFurniture()
    }

    fun loadFurniture() {
        viewModelScope.launch {
            _uiState.value = FurnitureUiState.Loading
            try {
                val result = repository.getAllFurnitureGroupedByTheme()

                result.onSuccess { grouped ->
                    grouped.forEach { (theme, items) ->
                        Log.d("FurnitureViewModel", "Tema: $theme, Muebles: ${items.size}")
                        items.forEach { furniture ->
                            Log.d(
                                "FurnitureViewModel",
                                "Mueble: ${furniture.name}, Precio: ${furniture.price}"
                            )
                        }
                    }
                    _uiState.value = FurnitureUiState.Success(grouped)
                }.onFailure { e ->
                    _uiState.value =
                        FurnitureUiState.Error("Error al cargar los muebles: ${e.message}")
                }
            } catch (e: Exception) {
                _uiState.value = FurnitureUiState.Error("Error al cargar los muebles: ${e.message}")
            }
        }
    }

    fun loadUserCoins(userId: String) {
        viewModelScope.launch {
            val result = userRepository.getUserCoins(userId) // Asumiendo que tienes esta función
            result.onSuccess { coinsValue ->
                _coins.value = coinsValue
                Log.d("FurnitureViewModel", "Monedas cargadas: $coinsValue")
            }.onFailure { exception ->
                Log.e("FurnitureViewModel", "Error al cargar las monedas: ${exception.message}")
            }
        }
    }

    fun canAfford(price: Int): Boolean {
        return _coins.value >= price
    }

    fun purchaseItem(userId: String, price: Int) {
        viewModelScope.launch {
            try {
                val newBalance = _coins.value - price
                db.collection("users").document(userId).update("coins", newBalance).await()
                _coins.value = newBalance
                Log.d("FurnitureViewModel", "Compra realizada, nuevo saldo: $newBalance")
            } catch (e: Exception) {
                Log.e("FurnitureViewModel", "Error en la compra: ${e.message}")
            }
        }
    }
}
