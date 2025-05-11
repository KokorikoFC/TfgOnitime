package com.example.tfgonitime.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgonitime.data.model.Pets
import com.example.tfgonitime.data.repository.PetsRepository
import com.example.tfgonitime.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// =========================================================================
// Estados de UI para Mascotas
// =========================================================================

sealed class AllPetsUiState {
    object Loading : AllPetsUiState()
    data class Success(val petsList: List<Pets>) : AllPetsUiState()
    data class Error(val message: String) : AllPetsUiState()
    object Empty : AllPetsUiState()
}

sealed class UserPetUiState {
    object Loading : UserPetUiState()
    data class Success(val selectedPet: Pets?) : UserPetUiState()
    data class Error(val message: String) : UserPetUiState()
    object NotLoggedIn : UserPetUiState()
}

class PetsViewModel : ViewModel() {

    private val petsRepository = PetsRepository()
    private val userRepository = UserRepository()

    private val _allPetsUiState = MutableStateFlow<AllPetsUiState>(AllPetsUiState.Loading)
    val allPetsUiState: StateFlow<AllPetsUiState> = _allPetsUiState

    private val _userPetUiState = MutableStateFlow<UserPetUiState>(UserPetUiState.NotLoggedIn)
    val userPetUiState: StateFlow<UserPetUiState> = _userPetUiState

    private val _pets = MutableStateFlow<List<Pets>>(emptyList())
    val pets: StateFlow<List<Pets>> = _pets

    init {
        loadAllPets()
        loadUserPet()
    }

    fun loadAllPets() {
        viewModelScope.launch {
            _allPetsUiState.value = AllPetsUiState.Loading

            try {
                val petsList = petsRepository.getAllPets()

                if (petsList.isEmpty()) {
                    _allPetsUiState.value = AllPetsUiState.Empty
                } else {
                    _allPetsUiState.value = AllPetsUiState.Success(petsList)
                    _pets.value = petsList
                }
            } catch (e: Exception) {
                val errorMsg = "Error al cargar mascotas: ${e.message}"
                _allPetsUiState.value = AllPetsUiState.Error(errorMsg)
            }
        }
    }

    fun loadUserPet() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            _userPetUiState.value = UserPetUiState.NotLoggedIn
            return
        }

        // Si la mascota ya está cargada, no la cargamos nuevamente.
        if (_userPetUiState.value is UserPetUiState.Loading || _userPetUiState.value is UserPetUiState.Success) {
            return
        }

        viewModelScope.launch {
            _userPetUiState.value = UserPetUiState.Loading
            val petIdResult = userRepository.getCurrentPetId(userId)
            val petId = petIdResult.getOrNull()

            if (petId == null) {
                _userPetUiState.value = UserPetUiState.Success(null)
            } else {
                val selectedPet = petsRepository.getPetById(petId)
                _userPetUiState.value = UserPetUiState.Success(selectedPet)
            }
        }
    }


    fun updateUserPetSelection(petId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            _userPetUiState.value = UserPetUiState.NotLoggedIn
            return
        }

        viewModelScope.launch {
            val result = userRepository.updateCurrentPetId(userId, petId)

            result.onSuccess {
                val localPet = _pets.value.firstOrNull { it.id == petId }
                _userPetUiState.value = UserPetUiState.Success(localPet)
            }.onFailure { error ->
                val errorMsg = "Error saving pet selection: ${error.message}"
                _userPetUiState.value = UserPetUiState.Error(errorMsg)
            }
        }
    }

}
