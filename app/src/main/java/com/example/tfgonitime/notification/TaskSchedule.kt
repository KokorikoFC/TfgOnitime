package com.example.tfgonitime.notification

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.tfgonitime.data.model.Task
import java.util.Calendar
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class TaskScheduler(private val context: Context) {

    private val workManager = WorkManager.getInstance(context)

    fun scheduleReminder(task: Task) {
        val reminder = task.reminder ?: return

        if (!reminder.isSet || reminder.time.isNullOrBlank() || reminder.days.isEmpty()) {
            Log.d("TaskScheduler", "Reminder not set, incomplete or disabled for task ${task.id}. Cancelling any existing work.")
            cancelReminder(task.id)
            return
        }

        cancelReminder(task.id)
        Log.d("TaskScheduler", "Scheduling work for task: ${task.id}")

        val timeParts = reminder.time.split(":")
        if (timeParts.size != 2) {
            Log.e("TaskScheduler", "Invalid time format: ${reminder.time}")
            return
        }

        val hour = timeParts[0].toIntOrNull()
        val minute = timeParts[1].toIntOrNull()

        if (hour == null || minute == null || hour !in 0..23 || minute !in 0..59) {
            Log.e("TaskScheduler", "Invalid hour or minute: ${reminder.time}")
            return
        }

        reminder.days.forEach { dayName ->
            dayName.toCalendarDay()?.let { dayOfWeek ->
                val now = Calendar.getInstance()
                val targetCalendar = Calendar.getInstance().apply {
                    timeZone = TimeZone.getDefault()
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                    set(Calendar.DAY_OF_WEEK, dayOfWeek)

                    if (before(now)) {
                        add(Calendar.WEEK_OF_YEAR, 1)
                    }
                }

                val initialDelay = targetCalendar.timeInMillis - System.currentTimeMillis()

                if (initialDelay < 0) {
                    Log.w("TaskScheduler", "Calculated initial delay for task ${task.id} on ${dayName} is negative. Skipping or adjusting to next occurrence.")
                    return@let
                }

                val inputData = workDataOf(
                    TASK_ID_KEY to task.id,
                    TASK_TITLE_KEY to task.title,
                    TASK_DESCRIPTION_KEY to task.description,
                    TASK_DAY_OF_WEEK_KEY to dayOfWeek
                )

                val workName = "${task.id}_${dayOfWeek}_reminder_worker"

                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .setRequiresBatteryNotLow(false)
                    .build()

                val periodicWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
                    repeatInterval = 7,
                    repeatIntervalTimeUnit = TimeUnit.DAYS
                )
                    .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .setConstraints(constraints)
                    .addTag(task.id)
                    .build()

                workManager.enqueueUniquePeriodicWork(
                    workName,
                    ExistingPeriodicWorkPolicy.UPDATE,
                    periodicWorkRequest
                )

                Log.d("TaskScheduler", "Scheduled unique periodic work for task ${task.id} on ${dayName} at ${reminder.time} with initial delay ${initialDelay}ms.")

            } ?: Log.w("TaskScheduler", "Unknown day name in reminder: $dayName for task ${task.id}")
        }
    }

    fun cancelReminder(taskId: String) {
        Log.d("TaskScheduler", "Attempting to cancel work for task $taskId")
        workManager.cancelAllWorkByTag(taskId)
        Log.d("TaskScheduler", "Cancelled all work tagged with $taskId.")
    }

    fun rescheduleAllReminders(tasks: List<com.example.tfgonitime.data.model.Task>) {
        Log.d("TaskScheduler", "Rescheduling reminders for all tasks.")
        tasks.filter { it.reminder?.isSet == true && !it.reminder.time.isNullOrBlank() && it.reminder.days.isNotEmpty() }
            .forEach { scheduleReminder(it) }
    }
}