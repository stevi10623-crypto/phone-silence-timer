package com.soundtimer.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import com.soundtimer.data.SoundCategory
import com.soundtimer.service.TimerService

class AndroidTimerActionHandler(private val context: Context) : TimerActionHandler {
    override fun startTimer(durationMillis: Long, categories: Set<SoundCategory>) {
        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_START
            putExtra(TimerService.EXTRA_DURATION, durationMillis)
            putExtra(TimerService.EXTRA_CATEGORIES, categories.map { it.name }.toTypedArray())
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    override fun stopTimer() {
        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_STOP
        }
        context.startService(intent)
    }

    override fun extendTimer(additionalMillis: Long) {
        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_EXTEND
        }
        context.startService(intent)
    }
}
