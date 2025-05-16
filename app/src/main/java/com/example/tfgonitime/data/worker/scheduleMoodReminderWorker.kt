package com.example.tfgonitime.data.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import java.util.concurrent.TimeUnit
import java.util.Calendar
import java.util.TimeZone

fun scheduleMoodReminderWorker(context: Context) {
    val currentDate = Calendar.getInstance()
    val dueDate = Calendar.getInstance()

    // Configura la hora deseada
    dueDate.set(Calendar.HOUR_OF_DAY, 16)
    dueDate.set(Calendar.MINUTE, 0)
    dueDate.set(Calendar.SECOND, 0)

    if (dueDate.before(currentDate)) {
        // Si ya pasó la hora de hoy, programa para mañana
        dueDate.add(Calendar.DAY_OF_MONTH, 1)
    }

    val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis

    val workRequest = PeriodicWorkRequestBuilder<MoodReminderWorker>(24, TimeUnit.HOURS)
        .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "mood_reminder",
        ExistingPeriodicWorkPolicy.REPLACE,
        workRequest
    )
}

