package com.soundtimer.util

import android.content.Context
import android.media.AudioManager
import com.soundtimer.data.SoundCategory
import com.soundtimer.data.VolumeState

class AndroidVolumeController(context: Context) : VolumeController {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val TAG = "AndroidVolumeController"

    override fun captureCurrentVolumes(): VolumeState {
        val state = VolumeState(
            ringerVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING),
            notificationVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION),
            mediaVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC),
            alarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM),
            systemVolume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM),
            ringerMode = audioManager.ringerMode
        )
        Log.d(TAG, "Captured volumes: $state")
        return state
    }

    override fun muteCategories(categories: Set<SoundCategory>) {
        Log.d(TAG, "Muting categories: $categories")
        categories.forEach { category ->
            // Use FLAG_ALLOW_RINGER_MODES to ensure the system recognizes the silence
            audioManager.setStreamVolume(categoryToStream(category), 0, AudioManager.FLAG_ALLOW_RINGER_MODES)
        }
    }

    override fun restoreVolumes(state: VolumeState, categories: Set<SoundCategory>) {
        Log.d(TAG, "Restoring volumes from state: $state")
        
        // 1. Restore the ringer mode first. This is crucial as it "unblocks" 
        // volume changes for streams like RING and NOTIFICATION if the phone was in Silent mode.
        try {
            audioManager.ringerMode = state.ringerMode
        } catch (e: Exception) {
            Log.e(TAG, "Failed to restore ringer mode", e)
        }

        // 2. Restore ALL captured streams, not just the ones in 'categories'.
        // Android often links Ring, Notification, and System volumes. Setting one to 0
        // might have muted others. Restoring all ensures the phone returns to its exact previous state.
        val streamsToRestore = listOf(
            AudioManager.STREAM_RING to state.ringerVolume,
            AudioManager.STREAM_NOTIFICATION to state.notificationVolume,
            AudioManager.STREAM_MUSIC to state.mediaVolume,
            AudioManager.STREAM_ALARM to state.alarmVolume,
            AudioManager.STREAM_SYSTEM to state.systemVolume
        )

        streamsToRestore.forEach { (stream, volume) ->
            try {
                // Use FLAG_ALLOW_RINGER_MODES to ensure the system allows exiting silent/vibrate
                audioManager.setStreamVolume(stream, volume, AudioManager.FLAG_ALLOW_RINGER_MODES)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to restore stream $stream to $volume", e)
            }
        }

        // 3. Re-apply ringer mode at the end as a safeguard
        try {
            audioManager.ringerMode = state.ringerMode
        } catch (e: Exception) {}
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
