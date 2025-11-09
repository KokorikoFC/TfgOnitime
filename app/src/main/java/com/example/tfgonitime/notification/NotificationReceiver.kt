package com.example.tfgonitime.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.tfgonitime.MainActivity // Reemplaza con tu Activity principal
import com.example.tfgonitime.R // Asegúrate de tener un drawable para el icono
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar
import java.util.concurrent.TimeUnit


class NotificationReceiver : BroadcastReceiver() {

    private val CHANNEL_ID = "task_reminder_channel"
    private val CHANNEL_NAME = "Task Reminders"
    private val CHANNEL_DESCRIPTION = "Notifications for task reminders"

    override fun onReceive(context: Context?, intent: Intent?) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Log.d("NotificationReceiver", "Usuario no autenticado. No se mostrará la notificación.")
            return
        }
        if (context == null || intent == null || intent.action != ACTION_TASK_REMINDER) {
            Log.w("NotificationReceiver", "Received unexpected intent action: ${intent?.action}")
            return
        }

        val taskId = intent.getStringExtra("TASK_ID") ?: return
        val taskTitle = intent.getStringExtra("TASK_TITLE") ?: "Recordatorio de Tarea"
        val taskDescription = intent.getStringExtra("TASK_DESCRIPTION") ?: "Es hora de tu tarea."

        Log.d("NotificationReceiver", "Received alarm for task: $taskId - $taskTitle")

        createNotificationChannel(context) // Create channel (safe to call repeatedly)

        val notificationManager = NotificationManagerCompat.from(context)


        val tapIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("TASK_ID_FROM_NOTIFICATION", taskId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }


        val tapPendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            taskId.hashCode() + 1000, // Use a different offset to distinguish from alarm codes
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.head_onigiri)
            .setContentTitle(taskTitle)
            .setContentText(taskDescription)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(tapPendingIntent)
            .setAutoCancel(true)

        val notificationId = taskId.hashCode() + (System.currentTimeMillis() % 10000).toInt()

        try {
            notificationManager.notify(notificationId, builder.build())
            Log.d("NotificationReceiver", "Notification shown for task: $taskId with notification ID $notificationId")



        } catch (e: SecurityException) {
            Log.e("NotificationReceiver", "SecurityException showing notification for task ${taskId}: ${e.message}. Check POST_NOTIFICATIONS permission.")
        } catch (e: Exception) {
            Log.e("NotificationReceiver", "Error showing notification for task ${taskId}: ${e.message}")
        }
    }

    // Create a Notification Channel for Android O and above
    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}