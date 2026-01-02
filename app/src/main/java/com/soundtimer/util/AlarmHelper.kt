package com.soundtimer.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.soundtimer.service.AlarmReceiver

/**
 * Helper class for managing exact alarms for timer completion.
 */
class AlarmHelper(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Schedules an exact alarm for when the timer should end.
     */
    fun scheduleTimerEnd(endTimeMillis: Long) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_TIMER_END
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_TIMER_END,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Use setExactAndAllowWhileIdle for Doze mode compatibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Check if we can schedule exact alarms on Android 12+
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    endTimeMillis,
                    pendingIntent
                )
            } else {
                // Fall back to inexact alarm
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    endTimeMillis,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                endTimeMillis,
                pendingIntent
            )
        }
    }

    /**
     * Cancels any scheduled timer alarm.
     */
    fun cancelTimerAlarm() {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_TIMER_END
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_TIMER_END,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    /**
     * Checks if the app can schedule exact alarms (Android 12+).
     */
    fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    companion object {
        const val ACTION_TIMER_END = "com.soundtimer.ACTION_TIMER_END"
        private const val REQUEST_CODE_TIMER_END = 1001
    }
}
