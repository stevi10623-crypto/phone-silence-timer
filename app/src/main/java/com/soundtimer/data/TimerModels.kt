package com.soundtimer.data

/**
 * Enum representing the different sound categories that can be muted.
 */
enum class SoundCategory {
    RINGER,        // Phone calls
    NOTIFICATION,  // Notification sounds
    MEDIA,         // Music, videos, games
    ALARM          // Alarm sounds (optional, usually left on)
}

/**
 * Data class representing the saved volume levels for each sound category.
 */
data class VolumeState(
    val ringerVolume: Int = 0,
    val notificationVolume: Int = 0,
    val mediaVolume: Int = 0,
    val alarmVolume: Int = 0
) {
    companion object {
        val EMPTY = VolumeState()
    }
}

/**
 * Data class representing the current timer state.
 */
data class TimerState(
    val isRunning: Boolean = false,
    val endTimeMillis: Long = 0L,
    val startTimeMillis: Long = 0L,
    val durationMillis: Long = 0L,
    val mutedCategories: Set<SoundCategory> = emptySet()
) {
    val remainingTimeMillis: Long
        get() = if (isRunning) {
            maxOf(0L, endTimeMillis - System.currentTimeMillis())
        } else {
            0L
        }

    val isExpired: Boolean
        get() = isRunning && System.currentTimeMillis() >= endTimeMillis

    companion object {
        val IDLE = TimerState()
    }
}

/**
 * Data class for user preferences.
 */
data class UserPreferences(
    val selectedCategories: Set<SoundCategory> = setOf(
        SoundCategory.RINGER,
        SoundCategory.NOTIFICATION
    ),
    val recentDurations: List<Long> = emptyList(), // in milliseconds
    val darkModeEnabled: Boolean? = null // null = follow system
)
