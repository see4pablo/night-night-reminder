package cl.see4pablo.nightnightreminder.notify

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object BedtimeScheduler {
    const val HOUR = 20
    const val MINUTE = 0
    private const val REQUEST_CODE = 2008

    fun nextTriggerDateTime(now: LocalDateTime = LocalDateTime.now()): LocalDateTime {
        val todayAtEight = LocalDateTime.of(now.toLocalDate(), LocalTime.of(HOUR, MINUTE))
        return if (now.isBefore(todayAtEight)) todayAtEight else todayAtEight.plusDays(1)
    }

    fun formatNextTrigger(now: LocalDateTime = LocalDateTime.now()): String {
        val next = nextTriggerDateTime(now)
        val fmt = DateTimeFormatter.ofPattern("EEE d MMM · HH:mm", Locale.forLanguageTag("es"))
        return next.format(fmt)
    }

    fun scheduleDaily(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        val pending = alarmPendingIntent(context)
        val triggerAt =
            nextTriggerDateTime()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAt,
                    pending,
                )
            }
            else -> {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pending)
            }
        }
    }

    fun canScheduleExact(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return false
        return alarmManager.canScheduleExactAlarms()
    }

    private fun alarmPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, BedtimeAlarmReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
