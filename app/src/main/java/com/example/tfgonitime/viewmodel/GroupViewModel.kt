package com.example.tfgonitime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgonitime.data.model.TaskGroup
import com.example.tfgonitime.data.repository.GroupRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class GroupViewModel : ViewModel() {

    private val groupRepository = GroupRepository()  // Instancia del repositorio que maneja los grupos

    // Estado para los grupos
    private val _groupsState = MutableStateFlow<List<TaskGroup>>(emptyList())  // Inicialmente vacío
    val groupsState: StateFlow<List<TaskGroup>> = _groupsState

    // Estado para manejar errores o estados de carga
    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    // Función para cargar todos los grupos de un usuario
    fun loadGroups(userId: String) {
        viewModelScope.launch {
            _loadingState.value = true
            val result = groupRepository.getGroups(userId)
            _loadingState.value = false

            result.onSuccess { groups ->
                _groupsState.value = groups
            }.onFailure {
                println("Error al cargar los grupos: ${it.message}")
            }
        }
    }

    suspend fun getGroupIdByName(userId: String, groupName: String): Result<String> {
        return groupRepository.getGroupIdByName(userId, groupName)
    }

    // Función para agregar un nuevo grupo
    fun addGroup(userId: String, group: TaskGroup, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        if (group.groupName.isBlank()) {
            onError("El nombre del grupo no puede estar vacío.")
            return
        }
        if (group.groupColor.isBlank()) {
            onError("Debes seleccionar un color para el grupo.")
            return
        }

        viewModelScope.launch {
            _loadingState.value = true
            val result = groupRepository.addGroup(userId, group)
            _loadingState.value = false

            result.onSuccess { groupId ->
                loadGroups(userId)  // Recargamos la lista de grupos
                onSuccess(groupId)  // Devolvemos el ID correcto
            }.onFailure { exception ->
                onError(exception.message ?: "Error desconocido")
            }
        }
    }


    // Función para eliminar un grupo
    fun deleteGroup(userId: String, groupId: String) {
        viewModelScope.launch {
            _loadingState.value = true

            // Primero actualizamos todas las tareas asociadas al grupo para que su groupId sea null
            val resultTasks = groupRepository.updateTasksWithNullGroupId(userId,groupId)

            // Si hubo algún error al actualizar las tareas, mostramos un mensaje y no eliminamos el grupo
            if (resultTasks.isFailure) {
                _loadingState.value = false
            }

            // Ahora eliminamos el grupo
            val resultGroup = groupRepository.deleteGroup(userId, groupId)

            _loadingState.value = false

            resultGroup.onSuccess {
                println("Grupo eliminado")
                loadGroups(userId)  // Recargamos la lista de grupos después de la eliminación
            }.onFailure {
                println("Error al eliminar el grupo: ${it.message}")
            }
        }
    }



}
