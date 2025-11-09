package com.example.tfgonitime.notification

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.tfgonitime.data.repository.TaskRepository
import com.google.firebase.auth.FirebaseAuth

class BootRescheduleWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        Log.d("BootRescheduleWorker", "Running BootRescheduleWorker to re-evaluate all alarms.")
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if (userId.isNullOrBlank()) {
            Log.e("BootRescheduleWorker", "Usuario no autenticado. No se pueden reprogramar las alarmas.")
            return Result.failure()
        }

        try {
            val taskRepository = TaskRepository()
            val result = taskRepository.getTasks(userId)

            result.onSuccess { tasks ->
                val taskScheduler = TaskScheduler(applicationContext)
                taskScheduler.rescheduleAllReminders(tasks)
                Log.d("BootRescheduleWorker", "Alarmas reprogramadas exitosamente para ${tasks.size} tareas.")
            }.onFailure { e ->
                Log.e("BootRescheduleWorker", "Error al recuperar tareas para reprogramar alarmas: ${e.message}")
                return Result.failure()
            }
            return Result.success()
        } catch (e: Exception) {
            Log.e("BootRescheduleWorker", "Excepción al reprogramar alarmas en BootRescheduleWorker: ${e.message}")
            return Result.failure()
        }
    }
}