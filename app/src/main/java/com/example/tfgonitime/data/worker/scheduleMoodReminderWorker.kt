package com.example.tfgonitime.data.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import java.util.concurrent.TimeUnit
import java.util.Calendar
import java.util.TimeZone

fun scheduleMoodReminderWorker(context: Context) {
    val workRequest = OneTimeWorkRequestBuilder<MoodReminderWorker>()
        .setInitialDelay(10, TimeUnit.SECONDS) // Ejecuta en 10 segundos para pruebas
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
        "mood_reminder",
        ExistingWorkPolicy.REPLACE,
        workRequest
    )
}

