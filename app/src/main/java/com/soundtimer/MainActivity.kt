package com.soundtimer

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.soundtimer.data.PreferencesManager
import com.soundtimer.data.TimerState
import com.soundtimer.service.TimerService
import com.soundtimer.ui.screens.OnboardingScreen
import com.soundtimer.ui.screens.TimerScreen
import com.soundtimer.ui.theme.SoundTimerTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var preferencesManager: PreferencesManager

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Permission result handled, UI will update
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferencesManager = PreferencesManager(this)

        setContent {
            SoundTimerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isDndAccessGranted by remember { mutableStateOf(checkDndAccess()) }
                    var showOnboarding by remember {
                        mutableStateOf(!preferencesManager.isOnboardingComplete() || !checkDndAccess())
                    }

                    // Timer state from service
                    val timerState by TimerService.timerStateFlow.collectAsState()

                    // Check permission when app resumes
                    LaunchedEffect(Unit) {
                        lifecycleScope.launch {
                            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                                isDndAccessGranted = checkDndAccess()
                                if (isDndAccessGranted && preferencesManager.isOnboardingComplete()) {
                                    showOnboarding = false
                                }

                                // Load persisted timer state on resume
                                val persistedState = preferencesManager.getTimerState()
                                if (persistedState.isRunning && !persistedState.isExpired) {
                                    TimerService.updateState(persistedState)
                                }
                            }
                        }
                    }

                    if (showOnboarding) {
                        OnboardingScreen(
                            isPermissionGranted = isDndAccessGranted,
                            onContinue = {
                                preferencesManager.setOnboardingComplete(true)
                                showOnboarding = false
                                requestNotificationPermissionIfNeeded()
                            }
                        )
                    } else {
                        TimerScreen(timerState = timerState)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Sync timer state from preferences
        val persistedState = preferencesManager.getTimerState()
        if (persistedState.isRunning) {
            if (persistedState.isExpired) {
                // Timer ended while app was in background - clear it
                preferencesManager.clearTimerState()
                TimerService.updateState(TimerState.IDLE)
            } else {
                TimerService.updateState(persistedState)
            }
        }
    }

    private fun checkDndAccess(): Boolean {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.isNotificationPolicyAccessGranted
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
