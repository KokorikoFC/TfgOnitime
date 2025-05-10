package com.example.tfgonitime.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.tfgonitime.data.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ReminderResetWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        // Obtener el userId desde los datos de entrada
        val userId = inputData.getString("USER_ID") ?: return@withContext Result.failure()

        // Inicializar el repositorio de tareas
        val taskRepository = TaskRepository()

        // Intentar obtener las tareas del usuario
        val result = taskRepository.getTasks(userId)

        // Verificar si se recuperaron las tareas correctamente
        if (result.isSuccess) {
            val today = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date()) // Ej: "lunes", "martes", etc.

            // Iterar sobre las tareas y verificar los recordatorios
            result.getOrNull()?.forEach { task ->
                val reminder = task.reminder

                // Verificar si el recordatorio está configurado, si contiene el día de hoy y si la tarea está completada
                if (reminder?.isSet == true &&
                    reminder.days.contains(today) &&
                    task.completed
                ) {
                    // Si cumple con las condiciones, marcar la tarea como no completada
                    taskRepository.updateTaskCompletion(userId, task.id, false)
                }
            }

            // Si todo se ejecutó con éxito
            return@withContext Result.success()
        } else {
            // Si no se pudieron obtener las tareas
            return@withContext Result.failure()
        }
    }
}
