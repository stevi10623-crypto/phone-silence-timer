package com.soundtimer.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.soundtimer.data.SoundCategory

/**
 * Rich card for a saved preset.
 */
@Composable
fun PresetCard(
    title: String,
    durationLabel: String,
    icon: ImageVector,
    iconColor: Color,
    mutedCategories: Set<SoundCategory>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Left Icon
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(iconColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Title & Duration
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = durationLabel,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Options Button
                IconButton(onClick = { /* More options */ }) {
                    Icon(
                        imageVector = Icons.Rounded.MoreHoriz,
                        contentDescription = "Options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Muted Channels Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "MUTED CHANNELS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SoundCategory.values().forEach { category ->
                        val isMuted = mutedCategories.contains(category)
                        val catIcon = when (category) {
                            SoundCategory.RINGER -> Icons.Rounded.Call
                            SoundCategory.NOTIFICATION -> Icons.Rounded.NotificationsOff
                            SoundCategory.MEDIA -> Icons.Rounded.MusicNote
                            SoundCategory.ALARM -> Icons.Rounded.Alarm
                            SoundCategory.SYSTEM -> Icons.Rounded.Smartphone
                        }
                        
                        val catColor = when (category) {
                            SoundCategory.RINGER -> com.soundtimer.ui.theme.CategoryCalls
                            SoundCategory.NOTIFICATION -> com.soundtimer.ui.theme.CategorySystem
                            SoundCategory.MEDIA -> com.soundtimer.ui.theme.CategoryMedia
                            SoundCategory.ALARM -> com.soundtimer.ui.theme.CategoryAlarms
                            SoundCategory.SYSTEM -> com.soundtimer.ui.theme.CategorySystem
                        }

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    if (isMuted) catColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = catIcon,
                                contentDescription = null,
                                tint = if (isMuted) catColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Toggle component for enabling/disabling a sound category.
 */
@Composable
fun SoundToggle(
    category: SoundCategory,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isTimerRunning: Boolean = false
) {
    val icon = when (category) {
        SoundCategory.RINGER -> Icons.Rounded.Call
        SoundCategory.NOTIFICATION -> Icons.Rounded.NotificationsOff
        SoundCategory.MEDIA -> Icons.Rounded.MusicNote
        SoundCategory.ALARM -> Icons.Rounded.Alarm
        SoundCategory.SYSTEM -> Icons.Rounded.Smartphone
    }

    val label = when (category) {
        SoundCategory.RINGER -> "Calls"
        SoundCategory.NOTIFICATION -> "System"
        SoundCategory.MEDIA -> "Media"
        SoundCategory.ALARM -> "Alarms"
        SoundCategory.SYSTEM -> "Device"
    }

    val description = when (category) {
        SoundCategory.RINGER -> "Block incoming calls"
        SoundCategory.NOTIFICATION -> "App notifications"
        SoundCategory.MEDIA -> "Silence music & videos"
        SoundCategory.ALARM -> "Only critical alerts"
        SoundCategory.SYSTEM -> "Feedback sounds"
    }

    // Category-specific colors from Color.kt
    val categoryColor = when (category) {
        SoundCategory.RINGER -> com.soundtimer.ui.theme.CategoryCalls
        SoundCategory.NOTIFICATION -> com.soundtimer.ui.theme.CategorySystem
        SoundCategory.MEDIA -> com.soundtimer.ui.theme.CategoryMedia
        SoundCategory.ALARM -> com.soundtimer.ui.theme.CategoryAlarms
        SoundCategory.SYSTEM -> com.soundtimer.ui.theme.CategorySystem
    }

    val categoryColorBg = when (category) {
        SoundCategory.RINGER -> com.soundtimer.ui.theme.CategoryCallsBg
        SoundCategory.NOTIFICATION -> com.soundtimer.ui.theme.CategorySystemBg
        SoundCategory.MEDIA -> com.soundtimer.ui.theme.CategoryMediaBg
        SoundCategory.ALARM -> com.soundtimer.ui.theme.CategoryAlarmsBg
        SoundCategory.SYSTEM -> com.soundtimer.ui.theme.CategorySystemBg
    }

    val backgroundColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.surface,
        animationSpec = tween(300),
        label = "backgroundColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (enabled) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        },
        animationSpec = tween(300),
        label = "contentColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = !isTimerRunning) { onToggle(!enabled) },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp, 
            if (enabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) 
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            color = categoryColorBg,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = categoryColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Switch(
                checked = enabled,
                onCheckedChange = { if (!isTimerRunning) onToggle(it) },
                enabled = !isTimerRunning,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }
    }
}

/**
 * Quick preset button for common durations.
 */
@Composable
fun PresetButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/**
 * Number picker for hours or minutes.
 */
@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    label: String,
    maxValue: Int,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = { if (value < maxValue) onValueChange(value + 1) },
            enabled = enabled && value < maxValue
        ) {
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowUp,
                contentDescription = "Increase",
                modifier = Modifier.size(32.dp)
            )
        }

        Text(
            text = value.toString().padStart(2, '0'),
            style = MaterialTheme.typography.displayMedium,
            color = if (enabled) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            }
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        IconButton(
            onClick = { if (value > 0) onValueChange(value - 1) },
            enabled = enabled && value > 0
        ) {
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = "Decrease",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

/**
 * Large countdown display showing remaining time.
 */
@Composable
fun CountdownDisplay(
    remainingTimeMillis: Long,
    totalDurationMillis: Long,
    modifier: Modifier = Modifier
) {
    val totalSeconds = remainingTimeMillis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    val progress = if (totalDurationMillis > 0) {
        remainingTimeMillis.toFloat() / totalDurationMillis.toFloat()
    } else {
        0f
    }

    Box(
        modifier = modifier
            .size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxSize(),
            strokeWidth = 12.dp,
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(1, '0')}:${seconds.toString().padStart(2, '0')}",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "remaining",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Status indicator showing current phone state.
 */
@Composable
fun StatusIndicator(
    isSilenced: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSilenced) {
            MaterialTheme.colorScheme.tertiaryContainer
        } else {
            MaterialTheme.colorScheme.primaryContainer
        },
        animationSpec = tween(300),
        label = "statusBg"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSilenced) {
            MaterialTheme.colorScheme.onTertiaryContainer
        } else {
            MaterialTheme.colorScheme.onPrimaryContainer
        },
        animationSpec = tween(300),
        label = "statusContent"
    )

    val icon = if (isSilenced) {
        Icons.Rounded.VolumeOff
    } else {
        Icons.Rounded.VolumeUp
    }

    val status = if (isSilenced) "SILENCED" else "NORMAL"

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = status,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = status,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor
            )
        }
    }
}
