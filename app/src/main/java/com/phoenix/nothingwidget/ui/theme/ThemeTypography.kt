package com.phoenix.nothingwidget.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
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
            titleWeight: FontWeight = FontWeight.Bold,
        ) = ThemeTypography(
            title = TextStyle(
                fontFamily = SpaceGrotesk,
                fontWeight = titleWeight,
                fontSize = titleSize,
                lineHeight = 34.sp,
                letterSpacing = (-0.8).sp,
            ),
            subtitle = TextStyle(
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp,
            ),
            section = TextStyle(
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 19.sp,
                lineHeight = 26.sp,
                letterSpacing = (-0.3).sp,
            ),
            cardTitle = TextStyle(
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                letterSpacing = (-0.4).sp,
            ),
            category = TextStyle(
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Normal,
                fontSize = 13.5.sp,
                lineHeight = 19.sp,
                letterSpacing = 0.1.sp,
            ),
            tag = TextStyle(
                fontFamily = SpaceMono,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                letterSpacing = 0.4.sp,
            ),
            chip = TextStyle(
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                lineHeight = 17.sp,
                letterSpacing = 0.1.sp,
            ),
            button = TextStyle(
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                letterSpacing = 0.1.sp,
            ),
        )

        val NothingPremium = base(
            titleSize = 28.sp,
            titleWeight = FontWeight.Bold,
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
