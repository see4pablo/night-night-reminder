package cl.see4pablo.nightnightreminder.notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        BedtimeNotifier.ensureChannel(context)
        BedtimeScheduler.scheduleDaily(context)
    }
}
