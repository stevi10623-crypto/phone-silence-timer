package com.soundtimer.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.soundtimer.data.PreferencesManager
import com.soundtimer.data.SoundCategory
import com.soundtimer.data.TimerState
import com.soundtimer.ui.TimerActionHandler
import com.soundtimer.ui.components.*
import com.soundtimer.ui.theme.GradientEnd
import com.soundtimer.ui.theme.GradientStart

/**
 * Main timer screen with time picker, sound toggles, and countdown display.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    timerState: TimerState,
    preferencesManager: PreferencesManager,
    actionHandler: TimerActionHandler,
    modifier: Modifier = Modifier
) {
    var hours by remember { mutableIntStateOf(0) }
    var minutes by remember { mutableIntStateOf(30) }

    var selectedCategories by remember {
        mutableStateOf(preferencesManager.getSelectedCategories())
    }

    val isRunning = timerState.isRunning
    val remainingMillis = timerState.remainingTimeMillis

    // Pulsing animation for the start button
    val infiniteTransition = rememberInfiniteTransition(label = "buttonPulse")
    val buttonScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (!isRunning) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buttonScale"
    )

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Phone Silence Timer",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Status Indicator
            StatusIndicator(isSilenced = isRunning)

            Spacer(modifier = Modifier.height(32.dp))

            // Timer Display Area
            AnimatedContent(
                targetState = isRunning,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith
                            fadeOut(animationSpec = tween(300))
                },
                label = "timerContent"
            ) { running ->
                if (running) {
                    // Countdown Display
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Time Remaining",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                CountdownDisplay(
                                    remainingTimeMillis = remainingMillis,
                                    totalDurationMillis = timerState.durationMillis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Extend Button
                        OutlinedButton(
                            onClick = {
                                actionHandler.extendTimer(15 * 60 * 1000L) // 15 mins
                            },
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = "Extend",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add 15 minutes")
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NumberPicker(
                            value = hours,
                            onValueChange = { hours = it },
                            label = "hrs",
                            maxValue = 23,
                            enabled = !isRunning
                        )

                        Text(
                            text = ":",
                            style = MaterialTheme.typography.displaySmall,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        NumberPicker(
                            value = minutes,
                            onValueChange = { minutes = it },
                            label = "min",
                            maxValue = 59,
                            enabled = !isRunning
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Presets
            if (!isRunning) {
                Text(
                    text = "Quick Presets",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PresetButton(
                        label = "15m",
                        onClick = { hours = 0; minutes = 15 },
                        modifier = Modifier.weight(1f),
                        enabled = !isRunning
                    )
                    PresetButton(
                        label = "30m",
                        onClick = { hours = 0; minutes = 30 },
                        modifier = Modifier.weight(1f),
                        enabled = !isRunning
                    )
                    PresetButton(
                        label = "1h",
                        onClick = { hours = 1; minutes = 0 },
                        modifier = Modifier.weight(1f),
                        enabled = !isRunning
                    )
                    PresetButton(
                        label = "2h",
                        onClick = { hours = 2; minutes = 0 },
                        modifier = Modifier.weight(1f),
                        enabled = !isRunning
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Sound Category Section
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isRunning) "Muted Sounds" else "Sounds to Mute",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (!isRunning) {
                        val allCategories = SoundCategory.entries.toSet()
                        TextButton(
                            onClick = {
                                selectedCategories = if (selectedCategories.size == allCategories.size) {
                                    emptySet()
                                } else {
                                    allCategories
                                }
                                preferencesManager.saveSelectedCategories(selectedCategories)
                            }
                        ) {
                            Text(
                                text = if (selectedCategories.size == allCategories.size) "Deselect All" else "Select All",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SoundCategory.entries.forEach { category ->
                        val isSelected = selectedCategories.contains(category)
                        SoundToggle(
                            category = category,
                            enabled = isSelected,
                            onToggle = {
                                selectedCategories = if (isSelected) {
                                    selectedCategories - category
                                } else {
                                    selectedCategories + category
                                }
                                preferencesManager.saveSelectedCategories(selectedCategories)
                            },
                            isTimerRunning = isRunning
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Start/Stop Button
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .scale(buttonScale)
                    .clip(CircleShape)
                    .background(
                        brush = if (isRunning) {
                            Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.error,
                                    MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                )
                            )
                        } else {
                            Brush.radialGradient(
                                colors = listOf(GradientStart, GradientEnd)
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        if (isRunning) {
                            actionHandler.stopTimer()
                        } else {
                            val durationMillis = (hours * 60L + minutes) * 60L * 1000L
                            if (durationMillis > 0 && selectedCategories.isNotEmpty()) {
                                actionHandler.startTimer(durationMillis, selectedCategories)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                        contentDescription = if (isRunning) "Stop Timer" else "Start Timer",
                        tint = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Text(
                text = if (isRunning) "Tap to Stop" else "Tap to Start",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
