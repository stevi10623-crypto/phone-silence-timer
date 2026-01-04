package com.soundtimer.ui

import com.soundtimer.data.SoundCategory

/**
 * Interface for handling timer actions from the UI.
 * This allows the shared UI to start/stop the timer without knowing about platform-specific services.
 */
interface TimerActionHandler {
    fun startTimer(durationMillis: Long, categories: Set<SoundCategory>)
    fun stopTimer()
    fun extendTimer(additionalMillis: Long)
}
