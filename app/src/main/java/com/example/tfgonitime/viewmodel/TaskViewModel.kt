package com.example.tfgonitime.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.example.tfgonitime.R
import com.example.tfgonitime.data.model.Task
import com.example.tfgonitime.data.repository.TaskRepository
import com.example.tfgonitime.data.repository.UserRepository
import com.example.tfgonitime.notification.TaskScheduler
import com.example.tfgonitime.worker.ReminderResetWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

// Change ViewModel to AndroidViewModel to get access to Application context
class TaskViewModel(application: Application, private val missionViewModel: MissionViewModel) : AndroidViewModel(application) {

    private val taskRepository = TaskRepository()
    private val userRepository = UserRepository()
    // Instantiate AlarmScheduler using the application context
    private val taskScheduler = TaskScheduler(application.applicationContext)

    // Estado para las tareas
    private val _tasksState = MutableStateFlow<List<Task>>(emptyList())
    val tasksState: StateFlow<List<Task>> = _tasksState

    // Estado para manejar errores o estados de carga
    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    // Function to add a task
    fun addTask(userId: String, task: Task, context: Context, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (task.title.isBlank()) {
            onError(context.getString(R.string.task_error_title))
            return
        }

        viewModelScope.launch {
            _loadingState.value = true
            val result = taskRepository.addTask(userId, task)
            _loadingState.value = false

            result.onSuccess { taskId ->

                val taskWithId = task.copy(id = taskId)
                taskScheduler.scheduleReminder(taskWithId)

                onSuccess()
                loadTasks(userId)
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

                Log.d("TaskViewModel", "Finished loading tasks.")
            }.onFailure {
                Log.e("TaskViewModel", "Error al obtener tareas: ${it.message}")
            }
        }
    }

    // Función para actualizar una tarea
    fun updateTask(userId: String, taskId: String, context: Context, updatedTask: Task, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (updatedTask.title.isBlank()) {
            onError(context.getString(R.string.task_error_title))
            return
        }

        val taskToSchedule = updatedTask.copy(id = taskId)

        viewModelScope.launch {
            _loadingState.value = true
            val result = taskRepository.updateTask(userId, taskId, taskToSchedule)
            _loadingState.value = false

            result.onSuccess {
                taskScheduler.scheduleReminder(taskToSchedule)

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
                taskScheduler.cancelReminder(taskId)
                Log.d("TaskViewModel", "Task $taskId deleted and alarms cancelled.")

                loadTasks(userId)
            } else {
                Log.e("TaskViewModel", "Error deleting task $taskId: ${result.exceptionOrNull()?.message}")
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

                } else {

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

    // Función para programar el Worker que reinicia los recordatorios
    // Función para programar el Worker que reinicia los recordatorios
    fun getDelayUntil0AM(): Long {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        // Establecer la hora a las 3 AM del día siguiente
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // Si ya pasó la hora de hoy, ajustamos al día siguiente
        if (calendar.timeInMillis <= now) {
            calendar.add(Calendar.DATE, 1)
        }

        return calendar.timeInMillis - now
    }

    fun scheduleReminderResetWorker(userId: String) {
        val initialDelay = getDelayUntil0AM()

        // Crea una instancia del Worker para que se ejecute periódicamente cada 24 horas
        val reminderResetWorkerRequest: WorkRequest =
            PeriodicWorkRequestBuilder<ReminderResetWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .setInputData(workDataOf("USER_ID" to userId))
                .build()

        // Enviar el Worker a WorkManager para que se ejecute periódicamente
        WorkManager.getInstance(getApplication()).enqueue(reminderResetWorkerRequest)
    }
}
