package com.soundtimer.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Premium Dark Theme - Slate & Violet Palette
 * Based on user-provided HTML/Tailwind configuration.
 */

// Background & Surface - Slate
val BackgroundSlate = Color(0xFF0F172A) // Slate 900
val SurfaceSlate = Color(0xFF1E293B)    // Slate 800
val BorderSlate = Color(0xFF334155)     // Slate 700

// Primary - Violet
val PrimaryViolet = Color(0xFF8B5CF6)   // Violet 500
val PrimaryHover = Color(0xFF7C3AED)    // Violet 600

// Gradient Button Colors (Violet -> Fuchsia)
val GradientStart = Color(0xFF7C3AED)   // Violet 600
val GradientEnd = Color(0xFFC026D3)     // Fuchsia 600

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
val Violet = PrimaryViolet
val VioletLight = Color(0xFFA78BFA)
val VioletDark = PrimaryHover
val BackgroundDark = BackgroundSlate
val SurfaceDark = SurfaceSlate
val CardDark = SurfaceSlate
val CardBorderDark = BorderSlate
val TextPrimary = TextWhite
val TextSecondary = TextSlate400
val CardBackgroundLight = Color(0xFFF8FAFC)
val CardBackgroundDark = SurfaceSlate

// Gradient references for screens
val GradientViolet = GradientStart
val GradientPink = GradientEnd
val OnSurfaceDark = TextWhite
