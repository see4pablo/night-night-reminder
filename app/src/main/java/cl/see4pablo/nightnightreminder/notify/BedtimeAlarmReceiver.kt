package cl.see4pablo.nightnightreminder.notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BedtimeAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        BedtimeNotifier.showPendingNotification(context, debug = false)
        // Re-arm for the next day (exact alarms are one-shot).
        BedtimeScheduler.scheduleDaily(context)
    }
}
