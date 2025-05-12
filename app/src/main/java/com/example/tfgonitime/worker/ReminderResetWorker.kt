package com.example.tfgonitime.worker

import android.content.Context
import android.util.Log
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
            // Convertir el día actual a mayúsculas en inglés
            val today = SimpleDateFormat("EEEE", Locale.ENGLISH).format(Date()).toUpperCase(Locale.ROOT)

            // Log para verificar que las tareas se están recuperando correctamente
            Log.d("ReminderResetWorker", "Tareas recuperadas para el usuario $userId")

            result.getOrNull()?.forEach { task ->
                val reminder = task.reminder

                // Log para verificar cada tarea y sus datos
                Log.d("ReminderResetWorker", "Revisando tarea: ${task.title}, completada: ${task.completed}, días: ${reminder?.days}")

                // Comprobar si reminder.days no es nulo y si contiene el día de hoy
                Log.d("ReminderResetWorker", "Revisando tarea: ${task.title}, completada=${task.completed}, días=${reminder?.days}, hoy=$today")
                if (reminder?.isSet == true && reminder.days != null && reminder.days.contains(today) && task.completed) {
                    Log.d("ReminderResetWorker", "Marcando tarea ${task.title} como no completada")
                    taskRepository.updateTaskCompletion(userId, task.id, false)
                }
            }

            // Si todo se ejecutó con éxito
            return@withContext Result.success()
        } else {
            // Si no se pudieron obtener las tareas
            Log.e("ReminderResetWorker", "Error al obtener las tareas para el usuario $userId")
            return@withContext Result.failure()
        }
    }
}
