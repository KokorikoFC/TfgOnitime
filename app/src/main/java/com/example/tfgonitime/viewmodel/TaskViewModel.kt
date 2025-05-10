package com.example.tfgonitime.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel // Import AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgonitime.data.model.Task
import com.example.tfgonitime.data.repository.TaskRepository
import com.example.tfgonitime.data.repository.UserRepository
import com.example.tfgonitime.notification.AlarmScheduler // Import AlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Change ViewModel to AndroidViewModel to get access to Application context
class TaskViewModel(application: Application, private val missionViewModel: MissionViewModel) : AndroidViewModel(application) {

    private val taskRepository = TaskRepository()
    private val userRepository = UserRepository()
    // Instantiate AlarmScheduler using the application context
    private val alarmScheduler = AlarmScheduler(application.applicationContext)

    // Estado para las tareas
    private val _tasksState = MutableStateFlow<List<Task>>(emptyList())
    val tasksState: StateFlow<List<Task>> = _tasksState

    // Estado para manejar errores o estados de carga
    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    // Function to add a task
    fun addTask(userId: String, task: Task, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (task.title.isBlank()) {
            onError("El título no puede estar vacío.")
            return
        }

        viewModelScope.launch {
            _loadingState.value = true
            val result = taskRepository.addTask(userId, task)
            _loadingState.value = false

            result.onSuccess { taskId ->
                // After successfully adding the task in the DB,
                // schedule the alarm if a reminder is set.
                // Use the task object with the generated ID for scheduling.
                val taskWithId = task.copy(id = taskId)
                alarmScheduler.scheduleReminder(taskWithId) // Schedule the alarm

                onSuccess()
                // Reload tasks to update the UI with the new task (including its generated ID)
                loadTasks(userId) // Or update the list manually
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

                // Optionally, you might want to reschedule all alarms here when tasks are loaded
                // on app startup or after a sync. This complements the BootReceiver.
                // Consider performance impact if you have many tasks.
                // alarmScheduler.rescheduleAllAlarms(tasks) // Uncomment if needed
                Log.d("TaskViewModel", "Finished loading tasks.")


            }.onFailure {
                Log.e("TaskViewModel", "Error al obtener tareas: ${it.message}")
            }
        }
    }


    // Función para actualizar una tarea
    fun updateTask(userId: String, taskId: String, updatedTask: Task, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (updatedTask.title.isBlank()) {
            onError("El título no puede estar vacío.")
            return
        }

        // Ensure the updated task object has the correct ID for scheduling/canceling
        val taskToSchedule = updatedTask.copy(id = taskId)

        viewModelScope.launch {
            _loadingState.value = true
            val result = taskRepository.updateTask(userId, taskId, taskToSchedule) // Use task with ID
            _loadingState.value = false

            result.onSuccess {
                // After successfully updating the task in the DB,
                // schedule or cancel the alarm based on the task's updated reminder
                alarmScheduler.scheduleReminder(taskToSchedule) // Schedule/update/cancel the alarm

                onSuccess()
                // Reload tasks to update the UI
                loadTasks(userId)

            }.onFailure {
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
            if (result.isSuccess) {
                // If deleted successfully, cancel any scheduled alarms for this task
                alarmScheduler.cancelReminder(taskId)
                Log.d("TaskViewModel", "Task $taskId deleted and alarms cancelled.")

                // Recargamos las tareas para actualizar la UI
                loadTasks(userId)
            } else {
                Log.e("TaskViewModel", "Error deleting task $taskId: ${result.exceptionOrNull()?.message}")
                // Handle deletion error, maybe show a toast
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
                // Actualiza Firestore para la tarea
                taskRepository.updateTaskCompletion(userId, taskId, isCompleted)

                // Update local state immediately for responsiveness
                _tasksState.value = _tasksState.value.map { task ->
                    if (task.id == taskId) task.copy(completed = isCompleted) else task
                }
                Log.d("TaskViewModel", "Task $taskId completion updated to $isCompleted")


                // Only if completed, update missions and counter
                if (isCompleted) {
                    userRepository.incrementTasksCompleted(userId)
                    Log.d("TaskViewModel", "User task completed count incremented for $userId")

                    // If this was a one-time reminder, you might cancel it here.
                    // For daily/repeating reminders, the rescheduling happens when the alarm fires.
                } else {
                    // If marked incomplete after being completed, you might need to
                    // reschedule a one-time alarm if that's your app's logic.
                }

                // Revisa si hay misiones relacionadas que deben completarse
                missionViewModel.checkMissionProgress(userId)
                Log.d("TaskViewModel", "Mission progress checked for $userId")


            } catch (e: Exception) {
                Log.e("TaskViewModel", "Error al actualizar el estado de la tarea $taskId: ${e.message}")
                // Optionally revert local UI state if DB update fails
            }
        }
    }

}