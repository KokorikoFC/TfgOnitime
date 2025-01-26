package com.example.tfgonitime.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgonitime.data.model.Task
import com.example.tfgonitime.data.repository.TaskRepository
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
    fun addTask(userId: String, task: Task, onSuccess: () -> Unit, onError: (String) -> Unit) {
        // Validar que el título y el grupo no estén vacíos
        if (task.title.isBlank()) {
            onError("El título no puede estar vacío.")
            return
        }

        if (task.groupId.isNullOrEmpty()) {
            onError("El grupo debe estar seleccionado.")
            return
        }

        // Si las validaciones pasan, continuar con la creación de la tarea
        viewModelScope.launch {
            _loadingState.value = true
            val result = taskRepository.addTask(userId, task)
            _loadingState.value = false

            result.onSuccess { taskId ->
                // Si la tarea se agrega exitosamente
                onSuccess()
            }.onFailure {
                // Si hubo un error al agregar la tarea
                onError("Error al agregar la tarea: ${it.message}")
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
    fun updateTask(userId: String, taskId: String, updatedTask: Task, onSuccess: () -> Unit, onError: (String) -> Unit) {
        // Validaciones previas
        if (updatedTask.title.isBlank()) {
            onError("El título de la tarea no puede estar vacío.")
            return
        }

        if (updatedTask.groupId.isNullOrEmpty()) {
            onError("El grupo de la tarea no puede estar vacío.")
            return
        }

        // Actualización de la tarea
        viewModelScope.launch {
            try {
                _loadingState.value = true
                val result = taskRepository.updateTask(userId, taskId, updatedTask)
                _loadingState.value = false

                result.onSuccess {
                    // Notificar éxito y recargar tareas
                    loadTasks(userId)
                    onSuccess()
                }.onFailure { error ->
                    // Notificar error
                    onError("Error al actualizar tarea: ${error.message}")
                }
            } catch (e: Exception) {
                _loadingState.value = false
                onError("Error inesperado: ${e.message}")
                Log.e("TaskViewModel", "Error al actualizar la tarea", e)
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

    fun getTaskById(taskId: String): Task? {
        return _tasksState.value.find { it.id == taskId }
    }


}

