package com.phoenix.nothingwidget.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Immutable
data class ThemeTypography(
    val title: TextStyle,
    val subtitle: TextStyle,
    val section: TextStyle,
    val cardTitle: TextStyle,
    val category: TextStyle,
    val tag: TextStyle,
    val chip: TextStyle,
    val button: TextStyle,
) {
    companion object {
        private fun base(
            titleSize: androidx.compose.ui.unit.TextUnit = 28.sp,
            titleWeight: FontWeight = FontWeight.SemiBold,
        ) = ThemeTypography(
            title = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = titleWeight,
                fontSize = titleSize,
                lineHeight = 34.sp,
                letterSpacing = (-0.3).sp,
            ),
            subtitle = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            ),
            section = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                lineHeight = 26.sp,
            ),
            cardTitle = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                lineHeight = 24.sp,
            ),
            category = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            ),
            tag = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.15.sp,
            ),
            chip = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.15.sp,
            ),
            button = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 20.sp,
            ),
        )

        val NothingPremium = base(
            titleSize = 28.sp,
            titleWeight = FontWeight.SemiBold,
        )

        val StudioClean = base(
            titleSize = 28.sp,
            titleWeight = FontWeight.SemiBold,
        )

        fun forMode(mode: AppThemeMode): ThemeTypography = when (mode) {
            AppThemeMode.NOTHING_PREMIUM -> NothingPremium
            AppThemeMode.STUDIO_CLEAN -> StudioClean
        }
    }
}

val LocalAppThemeTypography = staticCompositionLocalOf { ThemeTypography.NothingPremium }
