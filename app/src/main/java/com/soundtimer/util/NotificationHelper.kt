package com.soundtimer.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.soundtimer.MainActivity
import com.soundtimer.R
import com.soundtimer.service.TimerService

/**
 * Helper class for creating and managing notifications.
 */
class NotificationHelper(private val context: Context) {

    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = context.getString(R.string.notification_channel_description)
            setShowBadge(false)
        }
        notificationManager.createNotificationChannel(channel)

        // High priority channel for completion notification
        val completionChannel = NotificationChannel(
            CHANNEL_ID_COMPLETION,
            "Timer Complete",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notification when your timer ends"
            enableVibration(true)
        }
        notificationManager.createNotificationChannel(completionChannel)
    }

    /**
     * Creates a notification for the running timer foreground service.
     */
    fun createTimerNotification(remainingTimeFormatted: String): android.app.Notification {
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openPendingIntent = PendingIntent.getActivity(
            context, 0, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            context, 1, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val extendIntent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_EXTEND
        }
        val extendPendingIntent = PendingIntent.getService(
            context, 2, extendIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(context.getString(R.string.notification_timer_running))
            .setContentText(remainingTimeFormatted)
            .setSmallIcon(android.R.drawable.ic_lock_silent_mode)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(openPendingIntent)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                context.getString(R.string.notification_action_stop),
                stopPendingIntent
            )
            .addAction(
                android.R.drawable.ic_menu_add,
                context.getString(R.string.notification_action_extend),
                extendPendingIntent
            )
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    /**
     * Shows the timer completion notification.
     */
    fun showCompletionNotification() {
        if (!hasNotificationPermission()) {
            return
        }

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openPendingIntent = PendingIntent.getActivity(
            context, 0, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_COMPLETION)
            .setContentTitle(context.getString(R.string.notification_timer_complete_title))
            .setContentText(context.getString(R.string.notification_timer_complete_text))
            .setSmallIcon(android.R.drawable.ic_lock_silent_mode_off)
            .setAutoCancel(true)
            .setContentIntent(openPendingIntent)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(NOTIFICATION_ID_COMPLETION, notification)
    }

    /**
     * Updates the timer notification with new remaining time.
     */
    fun updateTimerNotification(remainingTimeFormatted: String) {
        if (!hasNotificationPermission()) {
            return
        }
        val notification = createTimerNotification(remainingTimeFormatted)
        notificationManager.notify(NOTIFICATION_ID_TIMER, notification)
    }

    /**
     * Cancels the timer notification.
     */
    fun cancelTimerNotification() {
        notificationManager.cancel(NOTIFICATION_ID_TIMER)
    }

    /**
     * Checks if notification permission is granted (Android 13+).
     */
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    /**
     * Formats milliseconds to a human-readable time string (HH:MM:SS or MM:SS).
     */
    fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    companion object {
        const val CHANNEL_ID = "sound_timer_channel"
        const val CHANNEL_ID_COMPLETION = "sound_timer_completion"
        const val NOTIFICATION_ID_TIMER = 1
        const val NOTIFICATION_ID_COMPLETION = 2
    }
}
