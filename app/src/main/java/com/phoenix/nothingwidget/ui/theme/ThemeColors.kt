package com.phoenix.nothingwidget.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ThemeColors(
    val background: Color,
    val backgroundMuted: Color,
    val surface: Color,
    val card: Color,
    val surfaceVariant: Color,
    val primary: Color,
    val primaryEnd: Color,
    val secondary: Color,
    val accent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val divider: Color,
    val onPrimary: Color,
    val chipUnselected: Color,
    val chipUnselectedText: Color,
    val previewSurface: Color,
    val cardBorder: Color,
    val previewBorder: Color,
    val shadow: Color,
    val patternPrimary: Color,
    val patternSecondary: Color,
    val isDark: Boolean,
) {
    companion object {
        val NothingPremium = ThemeColors(
            background = Color(0xFF101522),
            backgroundMuted = Color(0xFF0D1320),
            surface = Color(0xFF151C2C),
            card = Color(0xFF1A2235),
            surfaceVariant = Color(0xFF222C42),
            primary = Color(0xFF7C6CFF),
            primaryEnd = Color(0xFF7C6CFF),
            secondary = Color(0xFF9B8CFF),
            accent = Color(0xFF5B8CFF),
            textPrimary = Color(0xFFFFFFFF),
            textSecondary = Color(0xFFC5CEE3),
            textMuted = Color(0xFF8E99B3),
            divider = Color(0xFF283552),
            onPrimary = Color(0xFFFFFFFF),
            chipUnselected = Color(0xFF222C42),
            chipUnselectedText = Color(0xFFC5CEE3),
            previewSurface = Color(0xFF141B2B),
            cardBorder = Color(0xFF283552),
            previewBorder = Color(0xFF2A3550),
            shadow = Color(0xFF000000),
            patternPrimary = Color(0xFF7C6CFF),
            patternSecondary = Color(0xFF5B8CFF),
            isDark = true,
        )

        val StudioClean = ThemeColors(
            background = Color(0xFFF8F9FC),
            backgroundMuted = Color(0xFFF0F2F8),
            surface = Color(0xFFFFFFFF),
            card = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFEEF1F8),
            primary = Color(0xFF6E63FF),
            primaryEnd = Color(0xFF6E63FF),
            secondary = Color(0xFF8B7DFF),
            accent = Color(0xFF5B8CFF),
            textPrimary = Color(0xFF111827),
            textSecondary = Color(0xFF64748B),
            textMuted = Color(0xFF94A3B8),
            divider = Color(0xFFE2E8F0),
            onPrimary = Color(0xFFFFFFFF),
            chipUnselected = Color(0xFFEEF1F8),
            chipUnselectedText = Color(0xFF64748B),
            previewSurface = Color(0xFFF1F4FA),
            cardBorder = Color(0xFFE5E9F2),
            previewBorder = Color(0xFFE2E8F0),
            shadow = Color(0xFF94A3B8),
            patternPrimary = Color(0xFF6E63FF),
            patternSecondary = Color(0xFF94A3B8),
            isDark = false,
        )

        fun forMode(mode: AppThemeMode): ThemeColors = when (mode) {
            AppThemeMode.NOTHING_PREMIUM -> NothingPremium
            AppThemeMode.STUDIO_CLEAN -> StudioClean
        }
    }
}

val LocalAppThemeColors = staticCompositionLocalOf { ThemeColors.NothingPremium }
