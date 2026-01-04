package com.soundtimer.util

import com.soundtimer.data.SoundCategory
import com.soundtimer.data.VolumeState

/**
 * Interface for managing system volume levels in a platform-agnostic way.
 */
interface VolumeController {
    fun captureCurrentVolumes(): VolumeState
    fun muteCategories(categories: Set<SoundCategory>)
    fun restoreVolumes(state: VolumeState, categories: Set<SoundCategory>)
    fun getMaxVolume(category: SoundCategory): Int
    fun getCurrentVolume(category: SoundCategory): Int
    fun isMuted(category: SoundCategory): Boolean
}
