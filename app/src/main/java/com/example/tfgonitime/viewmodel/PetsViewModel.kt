package com.example.tfgonitime.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgonitime.data.model.Pets // Import Pets data class
import com.example.tfgonitime.data.repository.PetsRepository
import com.example.tfgonitime.data.repository.UserRepository // Use the corrected UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

import com.example.tfgonitime.viewmodel.AuthViewModel


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
    private val authViewModel = AuthViewModel()

    private val _allPetsUiState = MutableStateFlow<AllPetsUiState>(AllPetsUiState.Loading)
    val allPetsUiState: StateFlow<AllPetsUiState> = _allPetsUiState

    private val _userPetUiState = MutableStateFlow<UserPetUiState>(UserPetUiState.NotLoggedIn)
    val userPetUiState: StateFlow<UserPetUiState> = _userPetUiState

    private var isPetLoaded = false


    private val _pets = MutableStateFlow<List<Pets>>(emptyList())
    val pets: StateFlow<List<Pets>> = _pets


    private var currentUserId: String? = null

    init {
        viewModelScope.launch {
            authViewModel.userId.collectLatest { userId ->
                currentUserId = userId
                if (userId != null) {
                    Log.d(
                        "PetsViewModel",
                        "User ID changed to: $userId. Loading user pet and all pets."
                    )
                    loadAllPets()
                    loadUserPet()
                } else {
                    Log.d("PetsViewModel", "User ID is null. Clearing pet states.")
                    _userPetUiState.value = UserPetUiState.NotLoggedIn
                    // Decide si quieres que el catálogo se vacíe al desloguearse
                    _allPetsUiState.value =
                        AllPetsUiState.Empty // O Loading, o mantener el último éxito
                }
            }
        }

    }

    fun loadAllPets() {
        viewModelScope.launch {

            _allPetsUiState.value = AllPetsUiState.Loading
            Log.d("PetsViewModel", "Iniciando carga del catálogo de mascotas...")
            try {
                val petsList = petsRepository.getAllPets()
                Log.d(
                    "PetsViewModel",
                    "Catálogo de mascotas cargado con éxito: ${petsList.size} items."
                )
                if (petsList.isEmpty()) {
                    _allPetsUiState.value = AllPetsUiState.Empty
                } else {
                    _allPetsUiState.value = AllPetsUiState.Success(petsList)
                    _pets.value = petsList
                }
            } catch (e: Exception) {
                val errorMsg = "Error al cargar el catálogo de mascotas: ${e.message}"
                _allPetsUiState.value = AllPetsUiState.Error(errorMsg)
                Log.e("PetsViewModel", errorMsg, e)
            }
        }
    }



    fun loadUserPet() {
        if (isPetLoaded) return

        val userIdToUse = currentUserId ?: run {
            _userPetUiState.value = UserPetUiState.NotLoggedIn
            return
        }

        viewModelScope.launch {
            _userPetUiState.value = UserPetUiState.Loading
            val petIdResult = userRepository.getCurrentPetId(userIdToUse)
            val petId = petIdResult.getOrNull()

            if (petId == null) {
                _userPetUiState.value = UserPetUiState.Success(null)
                isPetLoaded = true
                return@launch
            }

            val selectedPet = petsRepository.getPetById(petId)
            _userPetUiState.value = UserPetUiState.Success(selectedPet)
            isPetLoaded = true
        }
    }



    fun updateUserPetSelection(petId: String) {
        val userIdToUse = currentUserId

        if (userIdToUse == null) {
            Log.w("PetsViewModel", "Cannot update pet selection: User is not authenticated.")
            if (_userPetUiState.value !is UserPetUiState.NotLoggedIn) {
                _userPetUiState.value = UserPetUiState.NotLoggedIn
            }
            return
        }

        viewModelScope.launch {
            val result = userRepository.updateCurrentPetId(userIdToUse, petId)

            result.onSuccess {
                Log.d("PetsViewModel", "Pet ID updated to $petId for user $userIdToUse")

                val localPet = _pets.value.firstOrNull { it.id == petId }
                _userPetUiState.value = UserPetUiState.Success(localPet)

            }.onFailure { error ->
                val errorMsg = "Error saving pet selection: ${error.message}"
                Log.e("PetsViewModel", errorMsg, error)
                _userPetUiState.value = UserPetUiState.Error(errorMsg)
            }
        }
    }

}