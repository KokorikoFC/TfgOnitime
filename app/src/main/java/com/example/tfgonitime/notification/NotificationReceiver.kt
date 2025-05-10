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
import java.util.Calendar
import java.util.concurrent.TimeUnit

// Note: TaskRepository and other components needed for rescheduling
// will need to be accessed safely within this receiver or a worker it triggers.
// Direct instantiation like below can be problematic.
// Consider using Hilt/dependency injection or WorkManager for robustness.
// val taskRepository = TaskRepository() // Example - DO NOT USE DIRECTLY LIKE THIS IN RECEIVER

class NotificationReceiver : BroadcastReceiver() {

    // Usaremos este ID de canal
    private val CHANNEL_ID = "task_reminder_channel"
    private val CHANNEL_NAME = "Task Reminders"
    private val CHANNEL_DESCRIPTION = "Notifications for task reminders"

    override fun onReceive(context: Context?, intent: Intent?) {
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

        // Create an Intent for the notification tap action
        // This intent could open a specific screen in your app related to the task
        val tapIntent = Intent(context, MainActivity::class.java).apply {
            // You might want to pass extras here to navigate to a specific screen
            putExtra("TASK_ID_FROM_NOTIFICATION", taskId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Use task ID hash or another unique value for the notification PendingIntent request code
        // This is separate from the AlarmManager PendingIntent request code
        val tapPendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            taskId.hashCode() + 1000, // Use a different offset to distinguish from alarm codes
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

        // Build the notification
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.head_onigiri) // <-- REPLACE THIS with your icon
            .setContentTitle(taskTitle)
            .setContentText(taskDescription)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(tapPendingIntent) // Set the tap action
            .setAutoCancel(true) // Dismiss notification when tapped

        // Use a unique ID for each notification. Task ID is a good candidate.
        // Adding system time makes it more unique if multiple alarms fire closely
        val notificationId = taskId.hashCode() + (System.currentTimeMillis() % 10000).toInt()

        try {
            notificationManager.notify(notificationId, builder.build())
            Log.d("NotificationReceiver", "Notification shown for task: $taskId with notification ID $notificationId")

            // --- IMPORTANT for REPEATING DAILY ALARMS ---
            // If you used setExactAndAllowWhileIdle for daily repeats,
            // you MUST reschedule the NEXT occurrence here.
            // This typically involves:
            // 1. Getting the task details (including reminder config) from the DB.
            // 2. Calculating the time for the NEXT relevant day.
            // 3. Calling alarmScheduler.scheduleReminder() for that task again.
            //
            // Doing database operations directly in a BroadcastReceiver can cause ANRs
            // if it takes too long. A common pattern is to start a WorkManager Worker
            // from here to handle the DB access and rescheduling in the background.
            //
            // Example (Conceptual - Requires proper Worker setup):
            // val workRequest = OneTimeWorkRequestBuilder<RescheduleAlarmWorker>()
            //    .setInputData(workDataOf("TASK_ID" to taskId))
            //    .build()
            // WorkManager.getInstance(context).enqueue(workRequest)

            // For simplicity in this example, I will NOT include the full rescheduling logic here.
            // You NEED to implement this part for daily repeats to work beyond the first trigger.

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