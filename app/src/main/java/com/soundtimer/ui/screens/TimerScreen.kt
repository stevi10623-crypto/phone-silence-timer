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
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material.icons.rounded.Nightlight
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
import com.soundtimer.ui.theme.GradientPink
import com.soundtimer.ui.theme.GradientViolet

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
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            if (!isRunning) {
                FloatingActionButton(
                    onClick = { /* Add New Preset */ },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.padding(bottom = 80.dp) // Avoid overlap with bottom bar
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Add Preset")
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(16.dp)
                    .padding(bottom = 16.dp)
            ) {
                Button(
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = if (isRunning) {
                                    Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.error))
                                } else {
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            com.soundtimer.ui.theme.GradientStart,
                                            com.soundtimer.ui.theme.GradientEnd
                                        )
                                    )
                                },
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isRunning) "Stop Focus Session" else "Start Focus Session",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = if (isRunning) Icons.Rounded.Stop else Icons.Rounded.ArrowForward,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
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

            // My Presets Section
            if (!isRunning) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Presets",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    IconButton(
                        onClick = { /* Add Preset */ },
                        modifier = Modifier
                            .size(32.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Add",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Preset Cards
                PresetCard(
                    title = "Deep Work",
                    durationLabel = "45 min",
                    icon = Icons.Rounded.Work,
                    iconColor = Color(0xFF8B5CF6), // Violet
                    mutedCategories = setOf(SoundCategory.NOTIFICATION, SoundCategory.SYSTEM),
                    onClick = { hours = 0; minutes = 45 }
                )

                Spacer(modifier = Modifier.height(12.dp))

                PresetCard(
                    title = "Sleep Mode",
                    durationLabel = "8 hr 00 min",
                    icon = Icons.Rounded.Nightlight,
                    iconColor = Color(0xFF3B82F6), // Blue
                    mutedCategories = setOf(SoundCategory.RINGER, SoundCategory.NOTIFICATION, SoundCategory.MEDIA, SoundCategory.SYSTEM),
                    onClick = { hours = 8; minutes = 0 }
                )

                Spacer(modifier = Modifier.height(12.dp))

                PresetCard(
                    title = "Reading",
                    durationLabel = "30 min",
                    icon = Icons.Rounded.MenuBook,
                    iconColor = Color(0xFF10B981), // Green
                    mutedCategories = setOf(SoundCategory.NOTIFICATION, SoundCategory.MEDIA),
                    onClick = { hours = 0; minutes = 30 }
                )

                Spacer(modifier = Modifier.height(12.dp))

                PresetCard(
                    title = "Power Nap",
                    durationLabel = "20 min",
                    icon = Icons.Rounded.FlashOn,
                    iconColor = Color(0xFFF59E0B), // Amber
                    mutedCategories = setOf(SoundCategory.NOTIFICATION),
                    onClick = { hours = 0; minutes = 20 }
                )

                Spacer(modifier = Modifier.height(12.dp))

                PresetCard(
                    title = "Dinner Mode",
                    durationLabel = "2 hr 00 min",
                    icon = Icons.Rounded.Restaurant,
                    iconColor = Color(0xFFEF4444), // Red
                    mutedCategories = setOf(SoundCategory.RINGER, SoundCategory.NOTIFICATION),
                    onClick = { hours = 2; minutes = 0 }
                )

                Spacer(modifier = Modifier.height(32.dp))
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

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
