package com.soundtimer.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.soundtimer.data.PreferencesManager
import com.soundtimer.util.NotificationHelper
import com.soundtimer.util.VolumeManager

/**
 * Broadcast receiver that handles device boot completion.
 * Restores timer state if a timer was running before the device was shut down.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {

            val preferencesManager = PreferencesManager(context)
            val timerState = preferencesManager.getTimerState()

            if (timerState.isRunning) {
                if (timerState.isExpired) {
                    // Timer should have ended while device was off - restore volumes now
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
                    TimerService.updateState(com.soundtimer.data.TimerState.IDLE)
                } else {
                    // Timer still has time remaining - restart the service
                    val serviceIntent = Intent(context, TimerService::class.java).apply {
                        action = TimerService.ACTION_RESTORE_FROM_BOOT
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent)
                    } else {
                        context.startService(serviceIntent)
                    }
                }
            }
        }
    }
}
