package com.soundtimer.data

/**
 * Interface for managing persistent storage of timer state, volume levels, and user preferences.
 * This allows platform-specific implementations (SharedPreferences for Android, NSUserDefaults for iOS).
 */
interface PreferencesManager {
    fun saveTimerState(state: TimerState)
    fun getTimerState(): TimerState
    fun clearTimerState()

    fun saveVolumeState(state: VolumeState)
    fun getVolumeState(): VolumeState?
    fun clearVolumeState()

    fun saveSelectedCategories(categories: Set<SoundCategory>)
    fun getSelectedCategories(): Set<SoundCategory>

    fun addRecentDuration(durationMillis: Long)
    fun getRecentDurations(): List<Long>

    fun setOnboardingComplete(complete: Boolean)
    fun isOnboardingComplete(): Boolean
}
