package com.soundtimer.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Premium Dark Theme - Slate & Violet Palette
 * Based on user-provided HTML/Tailwind configuration.
 */

// Background & Surface - Midnight
val BackgroundMidnight = Color(0xFF020617) // Slate 950
val SurfaceSlate = Color(0xFF0F172A)    // Slate 900
val BorderSlate = Color(0xFF1E293B)     // Slate 800

// Primary - Teal
val PrimaryTeal = Color(0xFF0D9488)   // Teal 600
val PrimaryHover = Color(0xFF0F766E)    // Teal 700

// Gradient Button Colors (Teal -> Sky)
val GradientStart = Color(0xFF0D9488)   // Teal 600
val GradientEnd = Color(0xFF0284C7)     // Sky 600

// Category Colors (from HTML)
val CategoryCalls = Color(0xFF0891B2)         // Cyan 600 (Icon)
val CategoryCallsBg = Color(0x3306B6D4)       // Cyan 500/20% (Bg)

val CategoryMedia = Color(0xFFC026D3)         // Fuchsia 600 (Icon)
val CategoryMediaBg = Color(0x33D946EF)       // Fuchsia 500/20% (Bg)

val CategoryAlarms = Color(0xFFD97706)        // Amber 600 (Icon)
val CategoryAlarmsBg = Color(0x33F59E0B)      // Amber 500/20% (Bg)

val CategorySystem = Color(0xFF059669)        // Emerald 600 (Icon)
val CategorySystemBg = Color(0x3310B981)      // Emerald 500/20% (Bg)

// Text Colors
val TextWhite = Color(0xFFFFFFFF)
val TextSlate400 = Color(0xFF94A3B8)
val TextSlate500 = Color(0xFF64748B)

// Status Colors
val StatusNormal = Color(0xFF22C55E)
val StatusSilenced = Color(0xFFF97316)

// Compatibility Layer (for current theme references)
val Teal = PrimaryTeal
val TealLight = Color(0xFF2DD4BF)
val TealDark = PrimaryHover
val BackgroundDark = BackgroundMidnight
val SurfaceDark = SurfaceSlate
val CardDark = SurfaceSlate
val CardBorderDark = BorderSlate
val TextPrimary = TextWhite
val TextSecondary = TextSlate400
val CardBackgroundLight = Color(0xFFF8FAFC)
val CardBackgroundDark = SurfaceSlate

// Gradient references for screens
val GradientTeal = GradientStart
val GradientBlue = GradientEnd
val OnSurfaceDark = TextWhite
