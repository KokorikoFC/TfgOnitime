package com.example.tfgonitime.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.example.tfgonitime.data.repository.DiaryRepository
import java.time.LocalDate

class MoodReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("MoodReminderWorker", "doWork() comenzó")  // Log de inicio
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            Log.d("MoodReminderWorker", "Usuario autenticado: $userId")
            val today = LocalDate.now().toString() // yyyy-MM-dd
            val diaryRepository = DiaryRepository()
            val result = diaryRepository.getMoods(
                userId,
                today.substring(0, 4),
                today.substring(5, 7)
            )

            val isRegistered = result.getOrDefault(emptyList()).any { it.moodDate == today }

            if (!isRegistered) {
                Log.d("MoodReminderWorker", "No registrado, enviando notificación")
                NotificationUtils.showMoodReminderNotification(applicationContext)
            } else {
                Log.d("MoodReminderWorker", "Ya registrado, no se enviará notificación")
            }
        } else {
            Log.d("MoodReminderWorker", "No hay usuario autenticado.")
        }

        return Result.success()
    }
}

