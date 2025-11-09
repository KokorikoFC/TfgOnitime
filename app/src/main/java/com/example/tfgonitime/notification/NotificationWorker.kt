package com.example.tfgonitime.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.tfgonitime.MainActivity
import com.example.tfgonitime.R
import com.example.tfgonitime.notification.*
import com.google.firebase.auth.FirebaseAuth

class NotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val CHANNEL_ID = "task_reminder_channel"
    private val CHANNEL_NAME = "Task Reminders"
    private val CHANNEL_DESCRIPTION = "Notifications for task reminders"

    override suspend fun doWork(): Result {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Log.d("NotificationWorker", "Usuario no autenticado. No se mostrará la notificación.")
            return Result.failure()
        }

        val taskId = inputData.getString(TASK_ID_KEY) ?: return Result.failure()
        val taskTitle = inputData.getString(TASK_TITLE_KEY) ?: "Recordatorio de Tarea"
        val taskDescription = inputData.getString(TASK_DESCRIPTION_KEY) ?: "Es hora de tu tarea."
        val taskDayOfWeek = inputData.getInt(TASK_DAY_OF_WEEK_KEY, -1)

        Log.d("NotificationWorker", "Received work for task: $taskId - $taskTitle on day: $taskDayOfWeek")

        createNotificationChannel(applicationContext)

        val notificationManager = NotificationManagerCompat.from(applicationContext)

        val tapIntent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("TASK_ID_FROM_NOTIFICATION", taskId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val tapPendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext,
            taskId.hashCode() + 1000 + taskDayOfWeek,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.head_onigiri)
            .setContentTitle(taskTitle)
            .setContentText(taskDescription)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(tapPendingIntent)
            .setAutoCancel(true)

        val notificationId = taskId.hashCode() + taskDayOfWeek + (System.currentTimeMillis() % 10000).toInt()

        try {
            notificationManager.notify(notificationId, builder.build())
            Log.d("NotificationWorker", "Notification shown for task: $taskId with notification ID $notificationId")
            return Result.success()
        } catch (e: SecurityException) {
            Log.e("NotificationWorker", "SecurityException showing notification for task ${taskId}: ${e.message}. Check POST_NOTIFICATIONS permission.")
            return Result.failure()
        } catch (e: Exception) {
            Log.e("NotificationWorker", "Error showing notification for task ${taskId}: ${e.message}")
            return Result.failure()
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}