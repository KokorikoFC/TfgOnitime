package com.example.tfgonitime.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.tfgonitime.data.repository.TaskRepository
import com.example.tfgonitime.notification.AlarmScheduler
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null || intent.action != Intent.ACTION_BOOT_COMPLETED) {
            if (intent?.action == Intent.ACTION_LOCKED_BOOT_COMPLETED) {
                Log.d("BootReceiver", "LOCKED_BOOT_COMPLETED: Esperando a que el usuario desbloquee el dispositivo.")
            } else {
                Log.w("BootReceiver", "Acción inesperada recibida: ${intent?.action}")
            }
            return
        }

        Log.d("BootReceiver", "Dispositivo reiniciado. Intentando reprogramar alarmas.")

        // ✅ Obtener el userId del usuario autenticado con Firebase
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if (userId.isNullOrBlank()) {
            Log.e("BootReceiver", "Usuario no autenticado. No se pueden reprogramar las alarmas.")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val taskRepository = TaskRepository()
                Log.d("BootReceiver", "Recuperando tareas para el usuario $userId")
                val result = taskRepository.getTasks(userId)

                result.onSuccess { tasks ->
                    val alarmScheduler = AlarmScheduler(context.applicationContext)
                    alarmScheduler.rescheduleAllAlarms(tasks)
                    Log.d("BootReceiver", "Alarmas reprogramadas exitosamente para ${tasks.size} tareas.")
                }.onFailure { e ->
                    Log.e("BootReceiver", "Error al recuperar tareas para reprogramar alarmas: ${e.message}")
                }
            } catch (e: Exception) {
                Log.e("BootReceiver", "Excepción al reprogramar alarmas: ${e.message}")
            }
        }
    }
}
