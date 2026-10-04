package com.phoenix.nothingwidget.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class ThemeDimensions(
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val cardPadding: Dp = 16.dp,
    val cornerRadius: Dp = 20.dp,
    val previewSize: Dp = 96.dp,
    val filterChipHeight: Dp = 38.dp,
    val filterChipSpacing: Dp = 10.dp,
    val iconButton: Dp = 40.dp,
    val headerToTabs: Dp = 14.dp,
    val tabsToContent: Dp = 28.dp,
    val itemSpacing: Dp = 16.dp,
) {
    companion object {
        val Default = ThemeDimensions()
    }
}

val LocalAppThemeDimensions = staticCompositionLocalOf { ThemeDimensions.Default }
