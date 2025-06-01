import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.tfgonitime.notification.BootRescheduleWorker

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            Log.w("BootReceiver", "Context or intent is null.")
            return
        }

        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Boot completed. Enqueuing BootRescheduleWorker.")
            val workRequest = OneTimeWorkRequestBuilder<BootRescheduleWorker>().build()
            WorkManager.getInstance(context).enqueue(workRequest)
        } else {
            Log.w("BootReceiver", "Received unexpected intent action: ${intent.action}")
        }
    }
}