package com.example.tfgonitime.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.tfgonitime.data.model.Task
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

const val ACTION_TASK_REMINDER = "com.example.tfgonitime.TASK_REMINDER"
const val DATA_SCHEME = "app"
const val DATA_HOST = "gonitime"
const val DATA_PATH_PREFIX = "/tasks/"


// Mapping function for day names
fun String.toCalendarDay(): Int? {
    return when (this.lowercase(Locale.getDefault())) {
        "lunes" -> Calendar.MONDAY
        "martes" -> Calendar.TUESDAY
        "miércoles", "miercoles" -> Calendar.WEDNESDAY
        "jueves" -> Calendar.THURSDAY
        "viernes" -> Calendar.FRIDAY
        "sábado", "sabado" -> Calendar.SATURDAY
        "domingo" -> Calendar.SUNDAY
        else -> null
    }
}

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleReminder(task: Task) {
        val reminder = task.reminder ?: return

        if (!reminder.isSet || reminder.time.isNullOrBlank() || reminder.days.isEmpty()) {
            Log.d("AlarmScheduler", "Reminder not set, incomplete or disabled for task ${task.id}. Cancelling any existing alarms.")
            cancelReminder(task.id)
            return
        }

        cancelReminder(task.id)
        Log.d("AlarmScheduler", "Scheduling alarms for task: ${task.id}")

        val timeParts = reminder.time.split(":")
        if (timeParts.size != 2) {
            Log.e("AlarmScheduler", "Invalid time format: ${reminder.time}")
            return
        }

        val hour = timeParts[0].toIntOrNull()
        val minute = timeParts[1].toIntOrNull()

        if (hour == null || minute == null || hour !in 0..23 || minute !in 0..59) {
            Log.e("AlarmScheduler", "Invalid hour or minute: ${reminder.time}")
            return
        }

        reminder.days.forEach { dayName ->
            dayName.toCalendarDay()?.let { dayOfWeek ->
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)

                    val currentDay = get(Calendar.DAY_OF_WEEK)
                    var daysUntilNext = dayOfWeek - currentDay
                    if (daysUntilNext < 0) {
                        daysUntilNext += 7
                    } else if (daysUntilNext == 0) {
                        if (timeInMillis <= System.currentTimeMillis()) {
                            daysUntilNext = 7
                        }
                    }
                    add(Calendar.DAY_OF_YEAR, daysUntilNext)
                }

                if (calendar.timeInMillis <= System.currentTimeMillis()) {

                    Log.w("AlarmScheduler", "Calculated time for task ${task.id} on ${dayName} is in the past. Skipping.")
                    return@let // Skip scheduling for this day
                }



                val requestCode = task.id.hashCode() + dayOfWeek

                val intent = Intent(context, NotificationReceiver::class.java).apply {
                    action = ACTION_TASK_REMINDER // Set the custom action
                    putExtra("TASK_ID", task.id)
                    putExtra("TASK_TITLE", task.title)
                    putExtra("TASK_DESCRIPTION", task.description) // Optional

                    data = android.net.Uri.parse("$DATA_SCHEME://$DATA_HOST$DATA_PATH_PREFIX${task.id}/day/$dayOfWeek")
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode, // Unique request code
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
                )

                try {

                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                    Log.d("AlarmScheduler", "Scheduled alarm for task ${task.id} on ${dayName} at ${reminder.time} with request code $requestCode for ${calendar.time}")

                } catch (e: SecurityException) {
                    Log.e("AlarmScheduler", "SecurityException scheduling alarm for task ${task.id}: ${e.message}. Consider using setAlarmClock or handle USE_EXACT_ALARM permission.")
                } catch (e: Exception) {
                    Log.e("AlarmScheduler", "Error scheduling alarm for task ${task.id}: ${e.message}")
                }
            } ?: Log.w("AlarmScheduler", "Unknown day name in reminder: $dayName for task ${task.id}")
        }
    }

    fun cancelReminder(taskId: String) {

        Log.d("AlarmScheduler", "Attempting to cancel alarms for task $taskId")

        val daysOfWeek = listOf(
            Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY,
            Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY
        )

        daysOfWeek.forEach { dayOfWeek ->
            val requestCode = taskId.hashCode() + dayOfWeek
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                action = ACTION_TASK_REMINDER
                data = android.net.Uri.parse("$DATA_SCHEME://$DATA_HOST$DATA_PATH_PREFIX${taskId}/day/$dayOfWeek")
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode, // Use the same request code
                intent,
                PendingIntent.FLAG_NO_CREATE or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0 // Use FLAG_NO_CREATE to check if it exists
            )

            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)

                Log.d("AlarmScheduler", "Cancelled alarm for task $taskId, dayOfWeek $dayOfWeek with request code $requestCode")
            } else {
                Log.d("AlarmScheduler", "No pending intent found for task $taskId, dayOfWeek $dayOfWeek with request code $requestCode")
            }
        }
    }

    fun rescheduleAllAlarms(tasks: List<Task>) {
        Log.d("AlarmScheduler", "Rescheduling alarms for all tasks after boot/app restart.")
        tasks.filter { it.reminder?.isSet == true && !it.reminder.time.isNullOrBlank() && it.reminder.days.isNotEmpty() }
            .forEach { scheduleReminder(it) }
    }
}