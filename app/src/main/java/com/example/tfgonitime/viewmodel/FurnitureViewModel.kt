package com.example.tfgonitime.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgonitime.data.model.Furniture // Asegúrate de importar tu data class Furniture
import com.example.tfgonitime.data.repository.FurnitureRepository // Asegúrate de importar tu FurnitureRepository
import com.example.tfgonitime.data.repository.UserRepository // Necesitas UserRepository para las monedas
import com.google.firebase.auth.FirebaseAuth // Necesitas FirebaseAuth para obtener el ID del usuario
import com.google.firebase.firestore.FieldValue // Necesitas FieldValue para incrementar/decrementar monedas
import com.google.firebase.firestore.FirebaseFirestore // Necesitas Firestore para la compra (si la mantienes aquí)
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Estado para el catálogo general de muebles (Tienda)
sealed class FurnitureCatalogUiState {
    object Loading : FurnitureCatalogUiState()
    data class Success(val furnitureByTheme: Map<String, List<Furniture>>) : FurnitureCatalogUiState()
    data class Error(val message: String) : FurnitureCatalogUiState()
}

// Estado para el inventario de muebles del usuario
sealed class UserInventoryUiState {
    object Loading : UserInventoryUiState()
    data class Success(val ownedFurniture: List<Furniture>) : UserInventoryUiState() // Lista de muebles POSSEÍDOS
    data class Error(val message: String) : UserInventoryUiState()
    object Empty : UserInventoryUiState() // Estado para cuando el inventario está vacío (corrección)
    object NotLoggedIn : UserInventoryUiState() // Estado para cuando no hay usuario logueado (corrección)
}
sealed class StoreFurnitureUiState {
    object Loading : StoreFurnitureUiState()
    data class Success(val furnitureList: Map<String, List<Furniture>>) : StoreFurnitureUiState() // Lista plana de muebles
    data class Error(val message: String) : StoreFurnitureUiState()
}


class FurnitureViewModel : ViewModel() {
    // Usamos FurnitureRepository para TODAS las operaciones de muebles (catálogo e inventario)
    private val furnitureRepository: FurnitureRepository = FurnitureRepository()
    // Usamos UserRepository solo para datos del usuario (monedas)
    private val userRepository: UserRepository = UserRepository()
    // Necesitas una instancia de Firestore si manejas transacciones/batches directamente aquí
    private val firestore = FirebaseFirestore.getInstance()

    // Estado de la tienda
    private val _storeUiState = MutableStateFlow<StoreFurnitureUiState>(StoreFurnitureUiState.Loading)
    val storeUiState: StateFlow<StoreFurnitureUiState> = _storeUiState

    // --- StateFlows ---
    // Para el catálogo completo de muebles (Tienda)
    private val _catalogUiState = MutableStateFlow<FurnitureCatalogUiState>(FurnitureCatalogUiState.Loading)
    val catalogUiState: StateFlow<FurnitureCatalogUiState> = _catalogUiState

    // Para el inventario del usuario actual
    private val _inventoryUiState = MutableStateFlow<UserInventoryUiState>(UserInventoryUiState.NotLoggedIn) // Empezar en NotLoggedIn o Loading si quieres cargar al inicio
    val inventoryUiState: StateFlow<UserInventoryUiState> = _inventoryUiState

    // Para las monedas del usuario
    private val _coins = MutableStateFlow(0)
    val coins: StateFlow<Int> = _coins
    // --- Fin StateFlows ---

    // Obtener el ID del usuario autenticado (se usa en load functions triggered by UI)
    private val userId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid


    init {
        // Cargar el catálogo general al iniciar el ViewModel (para la tienda)
        loadFurnitureCatalog()
        // La carga de monedas y el inventario se dispararán desde la UI (sus respectivas pantallas)
        // dependiendo de cuándo se necesiten y si hay un usuario logueado.
    }

    // Carga el catálogo completo de muebles agrupados por tema (para la Tienda)
    fun loadFurnitureCatalog() {
        viewModelScope.launch {
            _storeUiState.value = StoreFurnitureUiState.Loading
            Log.d("FurnitureViewModel", "Iniciando carga del catálogo de muebles...")

            val result = furnitureRepository.getAllFurnitureGroupedByTheme() // Llamada al repositorio para obtener los muebles agrupados

            result.onSuccess { furnitureGroupedByTheme ->
                _storeUiState.value = StoreFurnitureUiState.Success(furnitureGroupedByTheme)
                Log.d("FurnitureViewModel", "Catálogo de muebles cargado y agrupado por tema.")
            }.onFailure { e ->
                val errorMsg = "Error al cargar el catálogo de muebles: ${e.message}"
                _storeUiState.value = StoreFurnitureUiState.Error(errorMsg)
                Log.e("FurnitureViewModel", errorMsg, e)
            }
        }
    }




    // Carga los detalles de los muebles que posee un usuario específico (para el Inventario)
    fun loadUserInventory() {
        val currentUserId = userId
        if (currentUserId == null) {
            _inventoryUiState.value = UserInventoryUiState.NotLoggedIn
            Log.d("FurnitureViewModel", "loadUserInventory: Usuario no logueado.")
            return
        }

        viewModelScope.launch {
            _inventoryUiState.value = UserInventoryUiState.Loading
            Log.d("FurnitureViewModel", "Iniciando carga de inventario para el usuario: $currentUserId")

            // *** USAR LA FUNCIÓN CORRECTA DEL FURNITUREREPOSITORY QUE OBTIENE EL INVENTARIO COMPLETO ***
            val result = furnitureRepository.getUserInventoryFurniture(currentUserId)

            result.fold(
                onSuccess = { ownedFurnitureList ->
                    Log.d("FurnitureViewModel", "Inventario del usuario $currentUserId cargado con éxito: ${ownedFurnitureList.size} items.")
                    if (ownedFurnitureList.isEmpty()) {
                        _inventoryUiState.value = UserInventoryUiState.Empty
                    } else {
                        _inventoryUiState.value = UserInventoryUiState.Success(ownedFurnitureList)
                    }
                },
                onFailure = { e ->
                    val errorMsg = "Error al cargar el inventario para $currentUserId: ${e.localizedMessage}"
                    _inventoryUiState.value = UserInventoryUiState.Error(errorMsg)
                    Log.e("FurnitureViewModel", errorMsg, e)
                }
            )
        }
    }


    // Carga las monedas del usuario
    fun loadUserCoins(userId: String) {
        viewModelScope.launch {
            val result = userRepository.getUserCoins(userId)
            result.onSuccess { coinsValue ->
                _coins.value = coinsValue
            }.onFailure { exception ->
            }
        }
    }

    fun canAfford(price: Int): Boolean {
        return _coins.value >= price
    }

    fun purchaseItem(itemToBuy: Furniture) {
        val currentUserId = userId
        if (currentUserId == null) {

            return
        }
        if (!canAfford(itemToBuy.price)) {
            return
        }

        viewModelScope.launch {
            try {
                val userRef = firestore.collection("users").document(currentUserId)
                val inventoryRef = userRef.collection("inventory").document("available") // Referencia al documento de inventario

                firestore.runBatch { batch ->
                    batch.update(userRef, "coins", FieldValue.increment(-itemToBuy.price.toLong()))

                    batch.update(inventoryRef, "furniture", FieldValue.arrayUnion(itemToBuy.id))

                }.await()


                _coins.value = _coins.value - itemToBuy.price
                Log.d("FurnitureViewModel", "Compra exitosa para ${itemToBuy.name}. Nuevo saldo local: ${_coins.value}")

                loadUserInventory()

            } catch (e: Exception) {
                Log.e("FurnitureViewModel", "Error durante la transacción de compra para ${itemToBuy.name}: ${e.message}", e)

                loadUserCoins(currentUserId)
                loadUserInventory()
            }
        }
    }
}