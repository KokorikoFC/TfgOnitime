package com.example.tfgonitime.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgonitime.data.model.Task
import com.example.tfgonitime.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel(private val missionViewModel: MissionViewModel) : ViewModel() {

    private val taskRepository = TaskRepository()

    // Estado para las tareas
    private val _tasksState = MutableStateFlow<List<Task>>(emptyList())
    val tasksState: StateFlow<List<Task>> = _tasksState

    // Estado para manejar errores o estados de carga
    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    // Función para agregar una tarea
    fun addTask(userId: String, task: Task, onSuccess: () -> Unit, onError: (String) -> Unit) {
        // Validar que el título no esté vacío
        if (task.title.isBlank()) {
            onError("El título no puede estar vacío.")
            return
        }

        viewModelScope.launch {
            _loadingState.value = true
            val result = taskRepository.addTask(userId, task)  // Llamamos al repositorio para agregar la tarea
            _loadingState.value = false

            result.onSuccess { taskId ->
                // Aquí `taskId` es el id generado por Firestore, que ahora está en el objeto task
                onSuccess()
            }.onFailure {
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

                // Filtrar y mostrar tareas sin grupo
                val generalTasks = tasks.filter { task -> task.groupId.isNullOrEmpty() }
                Log.d("TaskViewModel", "Tareas sin grupo: ${generalTasks.size}")
            }.onFailure {
                Log.e("TaskViewModel", "Error al obtener tareas: ${it.message}")
            }
        }
    }


    // Función para actualizar una tarea
    fun updateTask(userId: String, taskId: String, updatedTask: Task, onSuccess: () -> Unit, onError: (String) -> Unit) {
        // Validar que el título no esté vacío
        if (updatedTask.title.isBlank()) {
            onError("El título no puede estar vacío.")
            return
        }

        // Permitir que el grupo sea vacío o nulo, si es necesario
        if (updatedTask.groupId == null || updatedTask.groupId.isBlank()) {
            // Si la tarea no tiene grupo, dejamos pasar esta validación
            // Esto puede aplicarse a tareas sin grupo, como "General"
        }

        // Si las validaciones pasan, continuar con la actualización de la tarea
        viewModelScope.launch {
            _loadingState.value = true
            val result = taskRepository.updateTask(userId, taskId, updatedTask)
            _loadingState.value = false

            result.onSuccess {
                // Si la tarea se actualiza exitosamente
                onSuccess()
            }.onFailure {
                // Si hubo un error al actualizar la tarea
                onError("Error al actualizar la tarea: ${it.message}")
            }
        }
    }



    // Función para eliminar una tarea
    fun deleteTask(userId: String, taskId: String) {
        viewModelScope.launch {
            _loadingState.value = true
            val result = taskRepository.deleteTask(userId, taskId)
            _loadingState.value = false
            // Aquí puedes manejar el resultado si es necesario (como mostrar un mensaje de éxito o error)
            if (result.isSuccess) {
                // Si se eliminó correctamente, recargamos las tareas
                loadTasks(userId)
            }
        }
    }


    fun getTaskById(taskId: String): Task? {
        return _tasksState.value.find { it.id == taskId }
    }

    // Función para actualizar el estado 'completed' de una tarea
    fun updateTaskCompletion(userId: String, taskId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                // Actualiza Firestore
                taskRepository.updateTaskCompletion(userId, taskId, isCompleted)

                // Actualiza la tarea en el estado local sin hacer una llamada adicional a Firestore
                _tasksState.value = _tasksState.value.map { task ->
                    if (task.id == taskId) {
                        task.copy(completed = isCompleted)  // Actualiza el campo 'completed' localmente
                    } else {
                        task
                    }
                }

                // Check for mission progress after task completion
                missionViewModel.checkMissionProgress(userId)

            } catch (e: Exception) {
                Log.e("TaskViewModel", "Error al actualizar el estado de la tarea: ${e.message}")
            }
        }
    }
}