package com.soundtimer.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.soundtimer.data.PreferencesManager
import com.soundtimer.data.TimerState
import com.soundtimer.util.AlarmHelper
import com.soundtimer.util.NotificationHelper
import com.soundtimer.util.VolumeManager

/**
 * Broadcast receiver that handles the timer completion alarm.
 * This is triggered by AlarmManager when the timer ends.
 */
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AlarmHelper.ACTION_TIMER_END) {
            val preferencesManager = PreferencesManager(context)
            val timerState = preferencesManager.getTimerState()

            if (timerState.isRunning) {
                // Restore volumes
                val volumeState = preferencesManager.getVolumeState()
                if (volumeState != null) {
                    val volumeManager = VolumeManager(context)
                    volumeManager.restoreVolumes(volumeState, timerState.mutedCategories)
                }

                // Show completion notification
                val notificationHelper = NotificationHelper(context)
                notificationHelper.showCompletionNotification()

                // Clear state
                preferencesManager.clearTimerState()
                preferencesManager.clearVolumeState()
                TimerService.updateState(TimerState.IDLE)

                // Stop the foreground service if running
                val stopIntent = Intent(context, TimerService::class.java).apply {
                    action = TimerService.ACTION_STOP
                }
                context.stopService(stopIntent)
            }
        }
    }
}
