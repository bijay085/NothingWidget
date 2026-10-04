package com.phoenix.nothingwidget.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class AppThemeMode {
    NOTHING_PREMIUM,
    STUDIO_CLEAN,
    ;

    companion object {
        fun fromStorage(value: String?): AppThemeMode {
            return entries.firstOrNull { it.name == value } ?: NOTHING_PREMIUM
        }
    }
}

@Composable
fun NothingWidgetTheme(
    themeMode: AppThemeMode = AppThemeMode.NOTHING_PREMIUM,
    content: @Composable () -> Unit,
) {
    val colors = remember(themeMode) { ThemeColors.forMode(themeMode) }
    val typography = remember(themeMode) { ThemeTypography.forMode(themeMode) }
    val dimensions = ThemeDimensions.Default

    val colorScheme = remember(colors) {
        if (colors.isDark) {
            darkColorScheme(
                primary = colors.primary,
                onPrimary = colors.onPrimary,
                secondary = colors.secondary,
                onSecondary = colors.onPrimary,
                tertiary = colors.accent,
                background = colors.background,
                onBackground = colors.textPrimary,
                surface = colors.surface,
                onSurface = colors.textPrimary,
                surfaceVariant = colors.surfaceVariant,
                onSurfaceVariant = colors.textSecondary,
                outline = colors.divider,
            )
        } else {
            lightColorScheme(
                primary = colors.primary,
                onPrimary = colors.onPrimary,
                secondary = colors.secondary,
                onSecondary = colors.onPrimary,
                tertiary = colors.accent,
                background = colors.background,
                onBackground = colors.textPrimary,
                surface = colors.surface,
                onSurface = colors.textPrimary,
                surfaceVariant = colors.surfaceVariant,
                onSurfaceVariant = colors.textSecondary,
                outline = colors.divider,
            )
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colors.background.toArgb()
            window.navigationBarColor = colors.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !colors.isDark
                isAppearanceLightNavigationBars = !colors.isDark
            }
        }
    }

    CompositionLocalProvider(
        LocalAppThemeColors provides colors,
        LocalAppThemeTypography provides typography,
        LocalAppThemeDimensions provides dimensions,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
        )
    }
}

/** Convenience accessors for composables. */
object AppTheme {
    val colors: ThemeColors
        @Composable
        get() = LocalAppThemeColors.current

    val typography: ThemeTypography
        @Composable
        get() = LocalAppThemeTypography.current

    val dimensions: ThemeDimensions
        @Composable
        get() = LocalAppThemeDimensions.current
}
