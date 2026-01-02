package com.soundtimer.util

import android.content.Context
import android.media.AudioManager
import com.soundtimer.data.SoundCategory
import com.soundtimer.data.VolumeState

/**
 * Utility class for managing system volume levels using AudioManager.
 */
class VolumeManager(context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    /**
     * Captures the current volume levels for all sound categories.
     */
    fun captureCurrentVolumes(): VolumeState {
        return VolumeState(
            ringerVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING),
            notificationVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION),
            mediaVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC),
            alarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)
        )
    }

    /**
     * Mutes the specified sound categories.
     */
    fun muteCategories(categories: Set<SoundCategory>) {
        categories.forEach { category ->
            val stream = categoryToStream(category)
            audioManager.setStreamVolume(stream, 0, 0)
        }
    }

    /**
     * Restores volume levels to the previously saved state.
     */
    fun restoreVolumes(state: VolumeState, categories: Set<SoundCategory>) {
        categories.forEach { category ->
            val stream = categoryToStream(category)
            val volume = when (category) {
                SoundCategory.RINGER -> state.ringerVolume
                SoundCategory.NOTIFICATION -> state.notificationVolume
                SoundCategory.MEDIA -> state.mediaVolume
                SoundCategory.ALARM -> state.alarmVolume
            }
            audioManager.setStreamVolume(stream, volume, 0)
        }
    }

    /**
     * Gets the maximum volume level for a specific category.
     */
    fun getMaxVolume(category: SoundCategory): Int {
        return audioManager.getStreamMaxVolume(categoryToStream(category))
    }

    /**
     * Gets the current volume level for a specific category.
     */
    fun getCurrentVolume(category: SoundCategory): Int {
        return audioManager.getStreamVolume(categoryToStream(category))
    }

    /**
     * Checks if a category is currently muted (volume = 0).
     */
    fun isMuted(category: SoundCategory): Boolean {
        return getCurrentVolume(category) == 0
    }

    private fun categoryToStream(category: SoundCategory): Int {
        return when (category) {
            SoundCategory.RINGER -> AudioManager.STREAM_RING
            SoundCategory.NOTIFICATION -> AudioManager.STREAM_NOTIFICATION
            SoundCategory.MEDIA -> AudioManager.STREAM_MUSIC
            SoundCategory.ALARM -> AudioManager.STREAM_ALARM
        }
    }
}
