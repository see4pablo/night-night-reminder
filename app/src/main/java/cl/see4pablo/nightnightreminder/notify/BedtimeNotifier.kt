package cl.see4pablo.nightnightreminder.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cl.see4pablo.nightnightreminder.MainActivity
import cl.see4pablo.nightnightreminder.R
import cl.see4pablo.nightnightreminder.data.ChecklistItem
import cl.see4pablo.nightnightreminder.data.ChecklistStore
import java.time.LocalDate

object BedtimeNotifier {
    const val CHANNEL_ID = "night_recaller_social"
    private const val NOTIFICATION_ID = 2008

    fun ensureChannel(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        // Soft, Instagram-like feed ping — not an alarm.
        val channel =
            NotificationChannel(
                CHANNEL_ID,
                "Night Recaller",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Avisos suaves de tareas pendientes"
                setShowBadge(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 40, 60, 40)
            }
        manager.createNotificationChannel(channel)
    }

    fun pendingToday(context: Context): List<ChecklistItem> {
        val today = LocalDate.now()
        return ChecklistStore(context).loadAll()[today].orEmpty().filterNot { it.done }
    }

    fun showPendingNotification(context: Context, debug: Boolean = false) {
        ensureChannel(context)
        val pending = pendingToday(context)
        val title = "Night Recaller"
        val body =
            when {
                pending.isEmpty() -> "Todo listo por hoy · night night"
                pending.size == 1 ->
                    "Tienes tareas pendientes · night night\n${pending.first().text}"
                else ->
                    "Tienes tareas pendientes · night night\n" +
                        pending.take(3).joinToString("\n") { "· ${it.text}" } +
                        if (pending.size > 3) "\n+${pending.size - 3} más" else ""
            }
        val short =
            if (pending.isEmpty()) {
                "Todo listo · night night"
            } else {
                "Tienes tareas pendientes · night night"
            }

        val open =
            PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val notification =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_night)
                .setContentTitle(title)
                .setContentText(if (debug) "Debug · $short" else short)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(open)
                .setColor(0xFFD62828.toInt())
                .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }
}
