package com.soundtimer.util

import android.content.Context
import android.media.AudioManager
import com.soundtimer.data.SoundCategory
import com.soundtimer.data.VolumeState

class AndroidVolumeController(context: Context) : VolumeController {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    override fun captureCurrentVolumes(): VolumeState {
        return VolumeState(
            ringerVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING),
            notificationVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION),
            mediaVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC),
            alarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM),
            systemVolume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM)
        )
    }

    override fun muteCategories(categories: Set<SoundCategory>) {
        categories.forEach { category ->
            audioManager.setStreamVolume(categoryToStream(category), 0, 0)
        }
    }

    override fun restoreVolumes(state: VolumeState, categories: Set<SoundCategory>) {
        categories.forEach { category ->
            val volume = when (category) {
                SoundCategory.RINGER -> state.ringerVolume
                SoundCategory.NOTIFICATION -> state.notificationVolume
                SoundCategory.MEDIA -> state.mediaVolume
                SoundCategory.ALARM -> state.alarmVolume
                SoundCategory.SYSTEM -> state.systemVolume
            }
            audioManager.setStreamVolume(categoryToStream(category), volume, 0)
        }
    }

    override fun getMaxVolume(category: SoundCategory): Int {
        return audioManager.getStreamMaxVolume(categoryToStream(category))
    }

    override fun getCurrentVolume(category: SoundCategory): Int {
        return audioManager.getStreamVolume(categoryToStream(category))
    }

    override fun isMuted(category: SoundCategory): Boolean {
        return getCurrentVolume(category) == 0
    }

    private fun categoryToStream(category: SoundCategory): Int {
        return when (category) {
            SoundCategory.RINGER -> AudioManager.STREAM_RING
            SoundCategory.NOTIFICATION -> AudioManager.STREAM_NOTIFICATION
            SoundCategory.MEDIA -> AudioManager.STREAM_MUSIC
            SoundCategory.ALARM -> AudioManager.STREAM_ALARM
            SoundCategory.SYSTEM -> AudioManager.STREAM_SYSTEM
        }
    }
}
