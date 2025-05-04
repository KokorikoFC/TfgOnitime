package com.example.tfgonitime.presentation.viewmodel // Keep your original package

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgonitime.data.model.Pets
import com.example.tfgonitime.data.repository.PetsRepository
import com.example.tfgonitime.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// Assuming AuthViewModel is in com.example.tfgonitime.viewmodel package
// You might need to adjust the import if it's different
import com.example.tfgonitime.viewmodel.AuthViewModel


class PetsViewModel(
    // Accept AuthViewModel and repositories as dependencies
    private val authViewModel: AuthViewModel,
    private val petsRepository: PetsRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _pets = MutableStateFlow<List<Pets>>(emptyList())
    val pets: StateFlow<List<Pets>> = _pets

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _currentUserPetId = MutableStateFlow<String?>(null)
    val currentUserPetId: StateFlow<String?> = _currentUserPetId

    private var currentUserId: String? = null // Internal state to hold the latest user ID

    init {
        fetchPets() // Fetch pets initially

        // Observe the userId from AuthViewModel
        viewModelScope.launch {
            authViewModel.userId.collectLatest { userId ->
                currentUserId = userId // Update internal user ID
                if (userId != null) {
                    Log.d("PetsViewModel", "User ID changed to: $userId. Loading user pet.")
                    loadUserPetId(userId) // Load pet ID when user becomes available
                } else {
                    Log.d("PetsViewModel", "User ID is null. Clearing current pet.")
                    _currentUserPetId.value = null // Clear pet ID if user logs out
                }
            }
        }
    }

    private fun fetchPets() {
        viewModelScope.launch {
            try {
                val petsList = petsRepository.getAllPets()
                _pets.value = petsList
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar las mascotas: ${e.message}"
                Log.e("PetsViewModel", "Error fetching pets: ${e.message}")
            }
        }
    }

    private fun loadUserPetId(userId: String) {
        viewModelScope.launch {
            userRepository.getCurrentPetId(userId)
                .onSuccess { petId ->
                    _currentUserPetId.value = petId
                }
                .onFailure { error ->
                    Log.e("PetsViewModel", "Error al obtener la ID de la mascota del usuario: ${error.message}")
                    _errorMessage.value = "Error loading user pet: ${error.message}"
                }
        }
    }

    fun updateUserPetId(petId: String) {
        val userIdToUse = currentUserId // Use the latest user ID

        if (userIdToUse == null) {
            Log.e("PetsViewModel", "Cannot update pet ID: User is not authenticated.")
            _errorMessage.value = "Cannot save pet: User not authenticated."
            return
        }

        viewModelScope.launch {
            userRepository.updateCurrentPetId(userIdToUse, petId)
                .onSuccess {
                    _currentUserPetId.value = petId // Update the local state on success
                    _errorMessage.value = null // Clear any previous errors
                    Log.d("PetsViewModel", "User pet ID updated successfully to $petId for user $userIdToUse")
                }
                .onFailure { error ->
                    Log.e("PetsViewModel", "Error al guardar la ID de la mascota del usuario: ${error.message}")
                    _errorMessage.value = "Error saving pet selection: ${error.message}"
                }
        }
    }
}

// You will NOT need a separate PetsViewModelFactory.kt file if you structure it this way
// and if AuthViewModel, PetsRepository, and UserRepository can be obtained
// via default ViewModel factories or other dependency injection mechanisms
// available in your project's setup.