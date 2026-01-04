package com.soundtimer.service

/**
 * Interface for handling notifications and alerts.
 * Allows common code to trigger alerts when the timer ends.
 */
interface NotificationHandler {
    fun showTimerStarted(durationMillis: Long)
    fun showTimerEnded()
    fun cancelNotification()
}
