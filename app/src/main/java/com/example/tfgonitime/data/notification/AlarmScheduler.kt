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

// Constants for Intent Action and Data Scheme
const val ACTION_TASK_REMINDER = "com.example.tfgonitime.TASK_REMINDER" // Use your package name
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
        val reminder = task.reminder ?: return // No reminder set

        if (!reminder.isSet || reminder.time.isNullOrBlank() || reminder.days.isEmpty()) {
            Log.d("AlarmScheduler", "Reminder not set, incomplete or disabled for task ${task.id}. Cancelling any existing alarms.")
            cancelReminder(task.id) // Cancel any existing alarms if reminder is disabled/incomplete
            return
        }

        // Cancel any previous alarms for this task first to avoid duplicates
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

                    // Set to the next occurrence of this day of the week and time
                    val currentDay = get(Calendar.DAY_OF_WEEK)
                    var daysUntilNext = dayOfWeek - currentDay
                    if (daysUntilNext < 0) {
                        daysUntilNext += 7 // Wrap around to next week
                    } else if (daysUntilNext == 0) {
                        // If it's today, check if the time has already passed
                        if (timeInMillis <= System.currentTimeMillis()) {
                            daysUntilNext = 7 // Schedule for next week
                        }
                    }
                    // Add the calculated number of days to the current date
                    add(Calendar.DAY_OF_YEAR, daysUntilNext)
                }

                // Ensure the time is in the future (should be handled by the daysUntilNext logic, but double check)
                if (calendar.timeInMillis <= System.currentTimeMillis()) {
                    // This case should ideally not happen with the logic above for daily reminders,
                    // but adding a log just in case or for one-time reminders.
                    Log.w("AlarmScheduler", "Calculated time for task ${task.id} on ${dayName} is in the past. Skipping.")
                    // If it were a one-time reminder, you might discard it or schedule for the very next minute.
                    return@let // Skip scheduling for this day
                }


                // Create a unique request code for the pending intent for this task and day
                // Combining task ID hash and day of week is a simple approach
                // Ensure the dayOfWeek is added in a way that makes it unique across days
                val requestCode = task.id.hashCode() + dayOfWeek

                val intent = Intent(context, NotificationReceiver::class.java).apply {
                    action = ACTION_TASK_REMINDER // Set the custom action
                    // Pass task details as extras
                    putExtra("TASK_ID", task.id)
                    putExtra("TASK_TITLE", task.title)
                    putExtra("TASK_DESCRIPTION", task.description) // Optional
                    // Add a unique data URI to make intents distinct for AlarmManager
                    // This is crucial for having multiple pending intents for the same receiver
                    // Use the defined constants for scheme, host, and path prefix
                    data = android.net.Uri.parse("$DATA_SCHEME://$DATA_HOST$DATA_PATH_PREFIX${task.id}/day/$dayOfWeek")
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode, // Unique request code
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
                )

                try {
                    // Use setExactAndAllowWhileIdle for more reliable alarms
                    // On Android 14+, this requires USE_EXACT_ALARM permission or eligibility
                    // setAlarmClock is another option for high priority, shown in system clock
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, // Use device's real time clock
                        calendar.timeInMillis,
                        pendingIntent
                    )
                    Log.d("AlarmScheduler", "Scheduled alarm for task ${task.id} on ${dayName} at ${reminder.time} with request code $requestCode for ${calendar.time}")

                } catch (e: SecurityException) {
                    // This can happen on Android 14+ if USE_EXACT_ALARM permission is not granted or app is not eligible
                    Log.e("AlarmScheduler", "SecurityException scheduling alarm for task ${task.id}: ${e.message}. Consider using setAlarmClock or handle USE_EXACT_ALARM permission.")
                    // Fallback or inform user
                } catch (e: Exception) {
                    Log.e("AlarmScheduler", "Error scheduling alarm for task ${task.id}: ${e.message}")
                }
            } ?: Log.w("AlarmScheduler", "Unknown day name in reminder: $dayName for task ${task.id}")
        }
    }

    fun cancelReminder(taskId: String) {
        // We need to cancel alarms for all possible days for this task
        // We need to reconstruct the pending intents with the correct request codes
        Log.d("AlarmScheduler", "Attempting to cancel alarms for task $taskId")

        // Assuming days can be any of the 7 days of the week (from Calendar constants)
        val daysOfWeek = listOf(
            Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY,
            Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY
        )

        daysOfWeek.forEach { dayOfWeek ->
            val requestCode = taskId.hashCode() + dayOfWeek
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                action = ACTION_TASK_REMINDER // Action must match
                // Data URI must match the one used for setting the alarm
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
                // Cancelling the PendingIntent itself is also good practice
                // pendingIntent.cancel() // Might not be strictly necessary after alarmManager.cancel but doesn't hurt
                Log.d("AlarmScheduler", "Cancelled alarm for task $taskId, dayOfWeek $dayOfWeek with request code $requestCode")
            } else {
                // This is expected if the alarm was never set for this day/task
                Log.d("AlarmScheduler", "No pending intent found for task $taskId, dayOfWeek $dayOfWeek with request code $requestCode")
            }
        }
    }

    // This method will be called by the BootReceiver to reschedule all tasks
    fun rescheduleAllAlarms(tasks: List<Task>) {
        Log.d("AlarmScheduler", "Rescheduling alarms for all tasks after boot/app restart.")
        tasks.filter { it.reminder?.isSet == true && !it.reminder.time.isNullOrBlank() && it.reminder.days.isNotEmpty() }
            .forEach { scheduleReminder(it) } // scheduleReminder handles canceling existing if any
    }
}