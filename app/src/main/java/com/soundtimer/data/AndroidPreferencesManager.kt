package com.soundtimer.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.datetime.Clock

class AndroidPreferencesManager(context: Context) : PreferencesManager {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )

    override fun saveTimerState(state: TimerState) {
        prefs.edit {
            putBoolean(KEY_TIMER_RUNNING, state.isRunning)
            putLong(KEY_TIMER_END_TIME, state.endTimeMillis)
            putLong(KEY_TIMER_START_TIME, state.startTimeMillis)
            putLong(KEY_TIMER_DURATION, state.durationMillis)
            putStringSet(KEY_MUTED_CATEGORIES, state.mutedCategories.map { it.name }.toSet())
        }
    }

    override fun getTimerState(): TimerState {
        val isRunning = prefs.getBoolean(KEY_TIMER_RUNNING, false)
        val endTime = prefs.getLong(KEY_TIMER_END_TIME, 0L)
        val startTime = prefs.getLong(KEY_TIMER_START_TIME, 0L)
        val duration = prefs.getLong(KEY_TIMER_DURATION, 0L)
        val categories = prefs.getStringSet(KEY_MUTED_CATEGORIES, emptySet())
            ?.mapNotNull { runCatching { SoundCategory.valueOf(it) }.getOrNull() }
            ?.toSet() ?: emptySet()

        val now = Clock.System.now().toEpochMilliseconds()
        val remaining = if (isRunning) maxOf(0L, endTime - now) else 0L

        return TimerState(
            isRunning = isRunning,
            endTimeMillis = endTime,
            startTimeMillis = startTime,
            durationMillis = duration,
            remainingTimeMillis = remaining,
            mutedCategories = categories
        )
    }

    override fun clearTimerState() {
        prefs.edit {
            putBoolean(KEY_TIMER_RUNNING, false)
            putLong(KEY_TIMER_END_TIME, 0L)
            putLong(KEY_TIMER_START_TIME, 0L)
            putLong(KEY_TIMER_DURATION, 0L)
            putStringSet(KEY_MUTED_CATEGORIES, emptySet())
        }
    }

    override fun saveVolumeState(state: VolumeState) {
        prefs.edit {
            putInt(KEY_VOLUME_RINGER, state.ringerVolume)
            putInt(KEY_VOLUME_NOTIFICATION, state.notificationVolume)
            putInt(KEY_VOLUME_MEDIA, state.mediaVolume)
            putInt(KEY_VOLUME_ALARM, state.alarmVolume)
            putInt(KEY_VOLUME_SYSTEM, state.systemVolume)
            putBoolean(KEY_VOLUME_SAVED, true)
        }
    }

    override fun getVolumeState(): VolumeState? {
        if (!prefs.getBoolean(KEY_VOLUME_SAVED, false)) {
            return null
        }
        return VolumeState(
            ringerVolume = prefs.getInt(KEY_VOLUME_RINGER, 0),
            notificationVolume = prefs.getInt(KEY_VOLUME_NOTIFICATION, 0),
            mediaVolume = prefs.getInt(KEY_VOLUME_MEDIA, 0),
            alarmVolume = prefs.getInt(KEY_VOLUME_ALARM, 0),
            systemVolume = prefs.getInt(KEY_VOLUME_SYSTEM, 0)
        )
    }

    override fun clearVolumeState() {
        prefs.edit {
            putBoolean(KEY_VOLUME_SAVED, false)
        }
    }

    override fun saveSelectedCategories(categories: Set<SoundCategory>) {
        prefs.edit {
            putStringSet(KEY_SELECTED_CATEGORIES, categories.map { it.name }.toSet())
        }
    }

    override fun getSelectedCategories(): Set<SoundCategory> {
        val saved = prefs.getStringSet(KEY_SELECTED_CATEGORIES, null)
        return if (saved != null) {
            saved.mapNotNull { runCatching { SoundCategory.valueOf(it) }.getOrNull() }.toSet()
        } else {
            setOf(SoundCategory.RINGER, SoundCategory.NOTIFICATION)
        }
    }

    override fun addRecentDuration(durationMillis: Long) {
        val recent = getRecentDurations().toMutableList()
        recent.remove(durationMillis)
        recent.add(0, durationMillis)
        val trimmed = recent.take(MAX_RECENT_DURATIONS)

        prefs.edit {
            putString(KEY_RECENT_DURATIONS, trimmed.joinToString(","))
        }
    }

    override fun getRecentDurations(): List<Long> {
        val saved = prefs.getString(KEY_RECENT_DURATIONS, null) ?: return emptyList()
        return saved.split(",").mapNotNull { it.toLongOrNull() }
    }

    override fun setOnboardingComplete(complete: Boolean) {
        prefs.edit {
            putBoolean(KEY_ONBOARDING_COMPLETE, complete)
        }
    }

    override fun isOnboardingComplete(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false)
    }

    companion object {
        private const val PREFS_NAME = "sound_timer_prefs"
        private const val KEY_TIMER_RUNNING = "timer_running"
        private const val KEY_TIMER_END_TIME = "timer_end_time"
        private const val KEY_TIMER_START_TIME = "timer_start_time"
        private const val KEY_TIMER_DURATION = "timer_duration"
        private const val KEY_MUTED_CATEGORIES = "muted_categories"
        private const val KEY_VOLUME_SAVED = "volume_saved"
        private const val KEY_VOLUME_RINGER = "volume_ringer"
        private const val KEY_VOLUME_NOTIFICATION = "volume_notification"
        private const val KEY_VOLUME_MEDIA = "volume_media"
        private const val KEY_VOLUME_ALARM = "volume_alarm"
        private const val KEY_VOLUME_SYSTEM = "volume_system"
        private const val KEY_SELECTED_CATEGORIES = "selected_categories"
        private const val KEY_RECENT_DURATIONS = "recent_durations"
        private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
        private const val MAX_RECENT_DURATIONS = 5
    }
}
