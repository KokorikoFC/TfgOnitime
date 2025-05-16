package com.example.tfgonitime.data.worker

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.tfgonitime.MainActivity
import com.example.tfgonitime.R

object NotificationUtils {
    private const val CHANNEL_ID = "mood_reminder_channel"
    private const val NOTIFICATION_ID = 1

    fun showMoodReminderNotification(context: Context) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigateTo", "home")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.head_onigiri)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            Log.d("MoodReminder", "Las notificaciones están habilitadas.")
            with(NotificationManagerCompat.from(context)) {
                notify(NOTIFICATION_ID, notification)
            }
        } else {
            Log.d("MoodReminder", "Las notificaciones NO están habilitadas.")
        }
    }

    private fun createNotificationChannel(context: Context) {

            Log.d("MoodReminder", "Canal de notificación creado o ya existe")

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            // Verificar si el canal ya existe antes de crearlo
            notificationManager.getNotificationChannel(CHANNEL_ID) ?: run {
                val name = "Recordatorio de diario"
                val descriptionText = "Te recuerda escribir en el diario cada día"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                notificationManager.createNotificationChannel(channel)
            }

    }

}
