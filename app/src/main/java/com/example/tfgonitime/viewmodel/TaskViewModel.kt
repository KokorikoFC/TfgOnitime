package com.example.tfgonitime.viewmodel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgonitime.data.model.Task
import com.example.tfgonitime.data.repository.TaskRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {

    private val taskRepository = TaskRepository()

    // Estado para las tareas
    private val _tasksState = MutableStateFlow<List<Task>>(emptyList())
    val tasksState: StateFlow<List<Task>> = _tasksState

    // Estado para manejar errores o estados de carga
    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    // Función para agregar una tarea
    fun addTask(userId: String, task: Task) {
        viewModelScope.launch {
            _loadingState.value = true
            val result = taskRepository.addTask(userId, task)
            _loadingState.value = false

            result.onSuccess { taskId ->
                // Tarea agregada exitosamente
                println("Tarea agregada con ID: $taskId")
                // Puedes actualizar el estado de tareas si es necesario
                loadTasks(userId)
            }.onFailure {
                // Hubo un error al agregar la tarea
                println("Error al agregar tarea: ${it.message}")
            }
        }
    }

    // Función para obtener todas las tareas de un usuario
    fun loadTasks(userId: String) {
        viewModelScope.launch {
            _loadingState.value = true
            val result = taskRepository.getTasks(userId)
            _loadingState.value = false

            result.onSuccess { tasks ->
                _tasksState.value = tasks
                Log.d("TaskViewModel", "Tareas cargadas: ${tasks.size}")
            }.onFailure {
                Log.e("TaskViewModel", "Error al obtener tareas: ${it.message}")
            }
        }
    }

    // Función para actualizar una tarea
    fun updateTask(userId: String, taskId: String, updatedTask: Task) {
        viewModelScope.launch {
            _loadingState.value = true
            val result = taskRepository.updateTask(userId, taskId, updatedTask)
            _loadingState.value = false

            result.onSuccess {
                // Tarea actualizada exitosamente
                println("Tarea actualizada")
                // Puedes recargar las tareas si es necesario
                loadTasks(userId)
            }.onFailure {
                // Hubo un error al actualizar la tarea
                println("Error al actualizar tarea: ${it.message}")
            }
        }
    }

    // Función para eliminar una tarea
    fun deleteTask(userId: String, taskId: String) {
        viewModelScope.launch {
            _loadingState.value = true
            val result = taskRepository.deleteTask(userId, taskId)
            _loadingState.value = false

            result.onSuccess {
                // Tarea eliminada exitosamente
                println("Tarea eliminada")
                // Puedes recargar las tareas si es necesario
                loadTasks(userId)
            }.onFailure {
                // Hubo un error al eliminar la tarea
                println("Error al eliminar tarea: ${it.message}")
            }
        }
    }
}

