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
import com.soundtimer.ui.theme.GradientBlue
import com.soundtimer.ui.theme.GradientTeal

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

    // Move presetConfigs to top level so they don't reset and are easily accessible
    val presetConfigs = remember {
        mutableStateMapOf(
            "Deep Work" to setOf(SoundCategory.NOTIFICATION, SoundCategory.SYSTEM),
            "Sleep Mode" to setOf(SoundCategory.RINGER, SoundCategory.NOTIFICATION, SoundCategory.MEDIA, SoundCategory.SYSTEM),
            "Reading" to setOf(SoundCategory.NOTIFICATION, SoundCategory.MEDIA),
            "Power Nap" to setOf(SoundCategory.NOTIFICATION),
            "Dinner Mode" to setOf(SoundCategory.RINGER, SoundCategory.NOTIFICATION)
        )
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

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Manual", "Presets")

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Phone Silence Timer",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    actions = {
                        Button(
                            onClick = { /* Go Pro Logic */ },
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .height(32.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                com.soundtimer.ui.theme.GradientTeal,
                                                com.soundtimer.ui.theme.GradientBlue
                                            )
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "GO PRO",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
                if (!isRunning) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.primary,
                        divider = {}
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            )
                        }
                    }
                }
            }
        },
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

            Spacer(modifier = Modifier.height(24.dp))

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
                    // Transition between Manual and Presets inside this area or handle below
                    if (selectedTab == 0) {
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
                    } else {
                        // Presets View
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            val presetAction = { h: Int, m: Int, title: String ->
                                val cats = presetConfigs[title] ?: emptySet()
                                hours = h
                                minutes = m
                                selectedCategories = cats
                                preferencesManager.saveSelectedCategories(cats)
                                selectedTab = 0
                            }

                            val onToggle = { title: String, cat: SoundCategory ->
                                val current = presetConfigs[title] ?: emptySet()
                                presetConfigs[title] = if (current.contains(cat)) current - cat else current + cat
                            }

                            PresetCard(
                                title = "Deep Work",
                                durationLabel = "45 min",
                                icon = Icons.Rounded.Work,
                                iconColor = Color(0xFF0D9488), // Teal
                                mutedCategories = presetConfigs["Deep Work"] ?: emptySet(),
                                onCategoryToggle = { onToggle("Deep Work", it) },
                                onClick = { presetAction(0, 45, "Deep Work") }
                            )

                            PresetCard(
                                title = "Sleep Mode",
                                durationLabel = "8 hr 00 min",
                                icon = Icons.Rounded.Nightlight,
                                iconColor = Color(0xFF3B82F6), // Blue
                                mutedCategories = presetConfigs["Sleep Mode"] ?: emptySet(),
                                onCategoryToggle = { onToggle("Sleep Mode", it) },
                                onClick = { presetAction(8, 0, "Sleep Mode") }
                            )

                            PresetCard(
                                title = "Reading",
                                durationLabel = "30 min",
                                icon = Icons.Rounded.MenuBook,
                                iconColor = Color(0xFF059669), // Emerald
                                mutedCategories = presetConfigs["Reading"] ?: emptySet(),
                                onCategoryToggle = { onToggle("Reading", it) },
                                onClick = { presetAction(0, 30, "Reading") }
                            )

                            PresetCard(
                                title = "Power Nap",
                                durationLabel = "20 min",
                                icon = Icons.Rounded.FlashOn,
                                iconColor = Color(0xFFD97706), // Amber
                                mutedCategories = presetConfigs["Power Nap"] ?: emptySet(),
                                onCategoryToggle = { onToggle("Power Nap", it) },
                                onClick = { presetAction(0, 20, "Power Nap") }
                            )

                            PresetCard(
                                title = "Dinner Mode",
                                durationLabel = "2 hr 00 min",
                                icon = Icons.Rounded.Restaurant,
                                iconColor = Color(0xFF0891B2), // Cyan
                                mutedCategories = presetConfigs["Dinner Mode"] ?: emptySet(),
                                onCategoryToggle = { onToggle("Dinner Mode", it) },
                                onClick = { presetAction(2, 0, "Dinner Mode") }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sound Category Section
            if (isRunning || selectedTab == 0) {
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
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Main Action Button (Moved from bottom bar for more compact layout)
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
                    .height(56.dp)
                    .scale(buttonScale),
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

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
