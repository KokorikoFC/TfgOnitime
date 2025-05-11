package com.example.tfgonitime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgonitime.data.model.Furniture
import com.example.tfgonitime.data.repository.FurnitureRepository
import com.example.tfgonitime.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// UI State for Furniture Catalog (Store)
sealed class FurnitureCatalogUiState {
    object Loading : FurnitureCatalogUiState()
    data class Success(val furnitureByTheme: Map<String, List<Furniture>>) : FurnitureCatalogUiState()
    data class Error(val message: String) : FurnitureCatalogUiState()
}

// UI State for the user's furniture inventory
sealed class UserInventoryUiState {
    object Loading : UserInventoryUiState()
    data class Success(val ownedFurniture: List<Furniture>) : UserInventoryUiState() // List of owned furniture
    data class Error(val message: String) : UserInventoryUiState()
    object Empty : UserInventoryUiState() // State for an empty inventory
    object NotLoggedIn : UserInventoryUiState() // State when the user is not logged in
}

sealed class StoreFurnitureUiState {
    object Loading : StoreFurnitureUiState()
    data class Success(val furnitureList: Map<String, List<Furniture>>) : StoreFurnitureUiState() // Flat list of furniture
    data class Error(val message: String) : StoreFurnitureUiState()
}

class FurnitureViewModel : ViewModel() {

    private val furnitureRepository: FurnitureRepository = FurnitureRepository()
    private val userRepository: UserRepository = UserRepository()
    private val firestore = FirebaseFirestore.getInstance()

    // Store UI State
    private val _storeUiState = MutableStateFlow<StoreFurnitureUiState>(StoreFurnitureUiState.Loading)
    val storeUiState: StateFlow<StoreFurnitureUiState> = _storeUiState

    // Furniture Catalog (Store)
    private val _catalogUiState = MutableStateFlow<FurnitureCatalogUiState>(FurnitureCatalogUiState.Loading)
    val catalogUiState: StateFlow<FurnitureCatalogUiState> = _catalogUiState

    // User's furniture inventory
    private val _inventoryUiState = MutableStateFlow<UserInventoryUiState>(UserInventoryUiState.NotLoggedIn) // Start with NotLoggedIn or Loading if you want to load at start
    val inventoryUiState: StateFlow<UserInventoryUiState> = _inventoryUiState

    // User's coins
    private val _coins = MutableStateFlow(0)
    val coins: StateFlow<Int> = _coins

    private val userId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    init {
        loadFurnitureCatalog()
    }

    // Load the full catalog of furniture grouped by theme (for the Store)
    fun loadFurnitureCatalog() {
        viewModelScope.launch {
            _storeUiState.value = StoreFurnitureUiState.Loading

            val result = furnitureRepository.getAllFurnitureGroupedByTheme() // Call repository to get grouped furniture

            result.onSuccess { furnitureGroupedByTheme ->
                _storeUiState.value = StoreFurnitureUiState.Success(furnitureGroupedByTheme)
            }.onFailure { e ->
                val errorMsg = " ${e.message}"
                _storeUiState.value = StoreFurnitureUiState.Error(errorMsg)
            }
        }
    }

    // Load the details of the furniture that a specific user owns (for the Inventory)
    fun loadUserInventory() {
        val currentUserId = userId
        if (currentUserId == null) {
            _inventoryUiState.value = UserInventoryUiState.NotLoggedIn
            return
        }

        viewModelScope.launch {
            _inventoryUiState.value = UserInventoryUiState.Loading

            val result = furnitureRepository.getUserInventoryFurniture(currentUserId)

            result.fold(
                onSuccess = { ownedFurnitureList ->
                    if (ownedFurnitureList.isEmpty()) {
                        _inventoryUiState.value = UserInventoryUiState.Empty
                    } else {
                        _inventoryUiState.value = UserInventoryUiState.Success(ownedFurnitureList)
                    }
                },
                onFailure = { e ->
                    val errorMsg = "Error loading inventory for $currentUserId: ${e.localizedMessage}"
                    _inventoryUiState.value = UserInventoryUiState.Error(errorMsg)
                }
            )
        }
    }

    // Load the user's coins
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
                val inventoryRef = userRef.collection("inventory").document("available")

                firestore.runBatch { batch ->
                    batch.update(userRef, "coins", FieldValue.increment(-itemToBuy.price.toLong()))
                    batch.update(inventoryRef, "furniture", FieldValue.arrayUnion(itemToBuy.id))
                }.await()

                _coins.value = _coins.value - itemToBuy.price

                loadUserInventory()

            } catch (e: Exception) {
                loadUserCoins(currentUserId)
                loadUserInventory()
            }
        }
    }

    // Load the selected furniture by the user (for the house)
    private val _selectedFurnitureMap = MutableStateFlow<Map<String, String>>(emptyMap())
    val selectedFurnitureMap: StateFlow<Map<String, String>> = _selectedFurnitureMap

    fun loadSelectedFurniture(userId: String) {
        viewModelScope.launch {
            val result = furnitureRepository.getSelectedFurniture(userId)
            result.onSuccess { slotMap ->
                _selectedFurnitureMap.value = slotMap
            }.onFailure { error ->
                _selectedFurnitureMap.value = emptyMap()
            }
        }
    }

    fun updateSelectedFurniture(slot: String, furnitureId: String) {
        val currentUserId = userId ?: return
        viewModelScope.launch {
            val result = furnitureRepository.updateSelectedFurniture(currentUserId, slot, furnitureId)
            if (result.isSuccess) {
                loadSelectedFurniture(currentUserId) // Reload after update
            }
        }
    }
}
