package com.example.tfgonitime.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.tfgonitime.data.repository.TaskRepository // Need access to repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // Check if context and intent are valid and if the action is boot completed
        if (context == null || intent == null || intent.action != Intent.ACTION_BOOT_COMPLETED) {
            if (intent?.action == Intent.ACTION_LOCKED_BOOT_COMPLETED) {
                Log.d("BootReceiver", "Received LOCKED_BOOT_COMPLETED. Alarms cannot be set until user unlocks.")
                // Handle locked boot if necessary - requires different approach or setting alarms after unlock
            } else {
                Log.w("BootReceiver", "Received unexpected intent action: ${intent?.action}")
            }
            return
        }

        Log.d("BootReceiver", "Received boot completed action. Attempting to reschedule alarms.")

        // IMPORTANT: You need a way to get the User ID here after boot.
        // This is a significant challenge as you don't have an active user session context.
        // Potential solutions:
        // 1. Store the last logged-in user ID securely (e.g., encrypted SharedPreferences)
        //    when the user logs in, and retrieve it here.
        // 2. If using Firebase Auth, attempt a silent sign-in here (more complex).
        // 3. If your app design implies only one user (less common), you might store
        //    a single user ID reference.
        // 4. If the user *must* open the app after reboot for alarms to work, you can
        //    remove the BootReceiver and handle rescheduling when the app starts.
        //
        // Replace "YOUR_USER_ID_HERE" with your actual logic to get the user ID.
        val userId = "YOUR_USER_ID_HERE" // <-- !!! REPLACE THIS !!!

        if (userId == "YOUR_USER_ID_HERE" || userId.isBlank()) {
            Log.e("BootReceiver", "User ID not available or is placeholder after boot. Cannot reschedule alarms.")
            // Consider informing the user that they need to open the app for alarms to work
            // or handle the case where no user is logged in.
            return
        }

        // Use a CoroutineScope to perform the database operation off the main thread
        // Ensure you handle potential errors (like no internet, Firebase not initialized)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val taskRepository = TaskRepository() // Instantiate repository - ensure it's safe to do here
                Log.d("BootReceiver", "Fetching tasks for user ID: $userId")
                val result = taskRepository.getTasks(userId) // Fetch all tasks

                result.onSuccess { tasks ->
                    val alarmScheduler = AlarmScheduler(context.applicationContext)
                    alarmScheduler.rescheduleAllAlarms(tasks) // Reschedule applicable alarms
                    Log.d("BootReceiver", "Successfully rescheduled alarms for ${tasks.size} tasks for user $userId.")
                }.onFailure { e ->
                    Log.e("BootReceiver", "Error fetching tasks for rescheduling for user $userId: ${e.message}")
                    // Consider adding a mechanism to retry later or notify the user of the issue.
                }
            } catch (e: Exception) {
                Log.e("BootReceiver", "Exception in BootReceiver background task for user $userId: ${e.message}")
                // Handle exceptions during repository access or scheduling.
            }
        }
    }
}