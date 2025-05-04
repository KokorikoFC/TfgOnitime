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


    // Mantendremos _pets si es necesario para otras partes que observan una lista plana
    // Sin embargo, con AllPetsUiState, esta lista podría ser redundante a largo plazo.
    // Por ahora, la mantenemos y la actualizamos desde loadAllPets.
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
        // Si quieres cargar el catálogo sin login, descomenta y ajusta la lógica en el LaunchedEffect
        // if (currentUserId == null) { loadAllPets() }
    }

    fun loadAllPets() {
        viewModelScope.launch {
            // Evitar recargar si ya estamos en éxito y la lista no está vacía (opcional optimización)
            // if (_allPetsUiState.value is AllPetsUiState.Success && (_allPetsUiState.value as AllPetsUiState.Success).petsList.isNotEmpty()) return@launch

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
                    _pets.value = petsList // Actualizar _pets si es observado por otras partes
                }
            } catch (e: Exception) {
                val errorMsg = "Error al cargar el catálogo de mascotas: ${e.message}"
                _allPetsUiState.value = AllPetsUiState.Error(errorMsg)
                Log.e("PetsViewModel", errorMsg, e)
            }
        }
    }

    fun loadUserPet() {
        val userIdToUse = currentUserId

        if (userIdToUse == null) {
            _userPetUiState.value = UserPetUiState.NotLoggedIn
            Log.d("PetsViewModel", "loadUserPet: Usuario no logueado.")
            return
        }

        viewModelScope.launch {
            // Evitar recargar si ya estamos cargando la mascota del usuario
            // if (_userPetUiState.value is UserPetUiState.Loading) return@launch

            _userPetUiState.value = UserPetUiState.Loading
            Log.d(
                "PetsViewModel",
                "Iniciando carga de mascota seleccionada para usuario: $userIdToUse"
            )

            val petIdResult = userRepository.getCurrentPetId(userIdToUse)
            val petId = petIdResult.getOrNull()

            if (petIdResult.isFailure) {
                val errorMsg =
                    "Error al obtener ID de mascota seleccionada: ${petIdResult.exceptionOrNull()?.localizedMessage}"
                _userPetUiState.value = UserPetUiState.Error(errorMsg)
                Log.e("PetsViewModel", errorMsg, petIdResult.exceptionOrNull())
                return@launch
            }

            // Si no hay petId (es null), actualizamos el estado a éxito con mascota null
            if (petId == null) {
                _userPetUiState.value = UserPetUiState.Success(null)
                Log.d(
                    "PetsViewModel",
                    "Usuario $userIdToUse no tiene mascota seleccionada (ID es null)."
                )
                return@launch
            }

            // Si tenemos un petId, obtener los detalles completos de esa mascota USANDO EL REPOSITORIO
            try {
                // **CORREGIDO: Llama a la nueva función getPetById en PetsRepository**
                val selectedPet = petsRepository.getPetById(petId)

                if (selectedPet != null) {
                    _userPetUiState.value = UserPetUiState.Success(selectedPet)
                    Log.d(
                        "PetsViewModel",
                        "Mascota seleccionada cargada con éxito: ${selectedPet.name} (${selectedPet.id})"
                    )
                } else {
                    // El ID guardado en el usuario no corresponde a una mascota existente en la colección 'pets'
                    _userPetUiState.value =
                        UserPetUiState.Success(null) // Consideramos que no hay mascota seleccionada válida
                    Log.w(
                        "PetsViewModel",
                        "ID de mascota seleccionada $petId para usuario $userIdToUse no encontrada en la colección 'pets'."
                    )
                    // Opcional: podrías considerar eliminar el userPetId incorrecto del documento del usuario aquí.
                }

            } catch (e: Exception) {
                val errorMsg =
                    "Error al obtener detalles de la mascota seleccionada $petId: ${e.localizedMessage}"
                _userPetUiState.value = UserPetUiState.Error(errorMsg)
                Log.e("PetsViewModel", errorMsg, e)
            }
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
            // Opcional: _userPetUiState.value = UserPetUiState.Loading

            val result = userRepository.updateCurrentPetId(userIdToUse, petId)

            result.onSuccess {
                Log.d(
                    "PetsViewModel",
                    "User pet ID updated successfully to $petId for user $userIdToUse"
                )
                loadUserPet() // Recargar la mascota seleccionada para actualizar el estado local con los detalles completos
            }.onFailure { error ->
                val errorMsg = "Error al guardar la ID de la mascota del usuario: ${error.message}"
                Log.e("PetsViewModel", errorMsg, error)
                _userPetUiState.value = UserPetUiState.Error("Error saving pet selection")
                loadUserPet() // Intentar recargar por si acaso el estado local no se actualizó correctamente
            }
        }
    }


}