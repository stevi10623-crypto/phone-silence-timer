package com.soundtimer.service

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import com.soundtimer.data.AndroidPreferencesManager
import com.soundtimer.data.PreferencesManager
import com.soundtimer.data.SoundCategory
import com.soundtimer.data.TimerState
import com.soundtimer.util.AlarmHelper
import com.soundtimer.util.NotificationHelper
import com.soundtimer.util.VolumeController
import com.soundtimer.util.AndroidVolumeController
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Foreground service that manages the timer countdown.
 * Runs in the background and updates the notification with remaining time.
 */
class TimerService : Service() {

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var volumeController: VolumeController
    private lateinit var alarmHelper: AlarmHelper
    private lateinit var notificationHelper: NotificationHelper

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var countdownJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        preferencesManager = AndroidPreferencesManager(this)
        volumeController = AndroidVolumeController(this)
        alarmHelper = AlarmHelper(this)
        notificationHelper = NotificationHelper(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            // Service restarted by system (START_STICKY)
            restoreFromBoot()
            return START_STICKY
        }

        when (intent.action) {
            ACTION_START -> {
                val durationMillis = intent.getLongExtra(EXTRA_DURATION, 0L)
                val categories = intent.getStringArrayExtra(EXTRA_CATEGORIES)
                    ?.mapNotNull { runCatching { SoundCategory.valueOf(it) }.getOrNull() }
                    ?.toSet() ?: emptySet()

                if (durationMillis > 0 && categories.isNotEmpty()) {
                    startTimer(durationMillis, categories)
                }
            }
            ACTION_STOP -> {
                stopTimer(restoreVolumes = true)
            }
            ACTION_EXTEND -> {
                extendTimer(EXTEND_DURATION_MILLIS)
            }
            ACTION_RESTORE_FROM_BOOT -> {
                restoreFromBoot()
            }
        }
        return START_STICKY
    }

    private fun startTimer(durationMillis: Long, categories: Set<SoundCategory>) {
        // Cancel any existing countdown
        countdownJob?.cancel()

        // Save current volumes before muting
        val currentVolumes = volumeController.captureCurrentVolumes()
        preferencesManager.saveVolumeState(currentVolumes)

        // Mute selected categories
        volumeController.muteCategories(categories)

        // Calculate end time
        val now = System.currentTimeMillis()
        val endTime = now + durationMillis

        // Save timer state
        val timerState = TimerState(
            isRunning = true,
            endTimeMillis = endTime,
            startTimeMillis = now,
            durationMillis = durationMillis,
            remainingTimeMillis = durationMillis,
            mutedCategories = categories
        )
        preferencesManager.saveTimerState(timerState)

        // Schedule alarm for timer end
        alarmHelper.scheduleTimerEnd(endTime)

        // Add to recent durations
        preferencesManager.addRecentDuration(durationMillis)

        // Update shared state
        _timerStateFlow.value = timerState

        // Start foreground service
        startForegroundService(timerState)

        // Start countdown updates
        startCountdown(endTime)
    }

    private fun startForegroundService(timerState: TimerState) {
        val remainingFormatted = notificationHelper.formatTime(timerState.remainingTimeMillis)
        val notification = notificationHelper.createTimerNotification(remainingFormatted)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NotificationHelper.NOTIFICATION_ID_TIMER,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NotificationHelper.NOTIFICATION_ID_TIMER, notification)
        }
    }

    private fun startCountdown(endTime: Long) {
        countdownJob = serviceScope.launch {
            // Get the base state once
            val baseState = preferencesManager.getTimerState()
            
            while (isActive) {
                val now = System.currentTimeMillis()
                val remaining = maxOf(0L, endTime - now)
                
                // Update shared state with explicit remaining time
                val tickingState = baseState.copy(
                    isRunning = true,
                    endTimeMillis = endTime,
                    remainingTimeMillis = remaining
                )
                _timerStateFlow.value = tickingState

                if (remaining <= 0) {
                    // Timer completed
                    withContext(Dispatchers.Main) {
                        onTimerComplete()
                    }
                    break
                }

                // Update notification
                val formatted = notificationHelper.formatTime(remaining)
                notificationHelper.updateTimerNotification(formatted)

                // Wait before next update
                delay(1000)
            }
        }
    }

    private fun restoreFromBoot() {
        val timerState = preferencesManager.getTimerState()
        if (timerState.isRunning) {
            if (timerState.isExpired) {
                // Timer should have ended while device was off
                onTimerComplete()
            } else {
                // Resume timer
                _timerStateFlow.value = timerState
                startForegroundService(timerState)
                startCountdown(timerState.endTimeMillis)

                // Re-schedule alarm
                alarmHelper.scheduleTimerEnd(timerState.endTimeMillis)
            }
        }
    }

    private fun extendTimer(additionalMillis: Long) {
        val currentState = preferencesManager.getTimerState()
        if (!currentState.isRunning) return

        countdownJob?.cancel()

        val newEndTime = currentState.endTimeMillis + additionalMillis
        val newState = currentState.copy(
            endTimeMillis = newEndTime,
            durationMillis = currentState.durationMillis + additionalMillis
        )
        preferencesManager.saveTimerState(newState)
        _timerStateFlow.value = newState

        // Update alarm
        alarmHelper.cancelTimerAlarm()
        alarmHelper.scheduleTimerEnd(newEndTime)

        // Restart countdown
        startCountdown(newEndTime)
    }

    private fun stopTimer(restoreVolumes: Boolean) {
        countdownJob?.cancel()
        alarmHelper.cancelTimerAlarm()

        if (restoreVolumes) {
            val volumeState = preferencesManager.getVolumeState()
            val timerState = preferencesManager.getTimerState()
            if (volumeState != null) {
                volumeController.restoreVolumes(volumeState, timerState.mutedCategories)
            }
        }

        preferencesManager.clearTimerState()
        preferencesManager.clearVolumeState()
        _timerStateFlow.value = TimerState.IDLE

        notificationHelper.cancelTimerNotification()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun onTimerComplete() {
        // Restore volumes
        val volumeState = preferencesManager.getVolumeState()
        val timerState = preferencesManager.getTimerState()
        if (volumeState != null) {
            volumeController.restoreVolumes(volumeState, timerState.mutedCategories)
        }

        // Show completion notification
        notificationHelper.showCompletionNotification()

        // Clear state
        preferencesManager.clearTimerState()
        preferencesManager.clearVolumeState()
        _timerStateFlow.value = TimerState.IDLE

        // Stop service
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        countdownJob?.cancel()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "com.soundtimer.ACTION_START"
        const val ACTION_STOP = "com.soundtimer.ACTION_STOP"
        const val ACTION_EXTEND = "com.soundtimer.ACTION_EXTEND"
        const val ACTION_RESTORE_FROM_BOOT = "com.soundtimer.ACTION_RESTORE_FROM_BOOT"

        const val EXTRA_DURATION = "extra_duration"
        const val EXTRA_CATEGORIES = "extra_categories"

        private const val EXTEND_DURATION_MILLIS = 15 * 60 * 1000L // 15 minutes

        // Shared state flow for UI updates
        private val _timerStateFlow = MutableStateFlow(TimerState.IDLE)
        val timerStateFlow: StateFlow<TimerState> = _timerStateFlow.asStateFlow()

        fun updateState(state: TimerState) {
            _timerStateFlow.value = state
        }
    }
}
