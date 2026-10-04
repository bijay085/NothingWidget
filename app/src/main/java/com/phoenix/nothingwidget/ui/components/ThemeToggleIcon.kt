package com.phoenix.nothingwidget.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.phoenix.nothingwidget.ui.theme.AppTheme
import com.phoenix.nothingwidget.ui.theme.AppThemeMode
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ThemeToggleIcon(
    themeMode: AppThemeMode,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    val iconTint = colors.textPrimary
    val showSun = themeMode == AppThemeMode.NOTHING_PREMIUM

    IconButton(
        onClick = onToggle,
        modifier = modifier.size(44.dp),
    ) {
        if (showSun) {
            SunIcon(tint = iconTint)
        } else {
            MoonIcon(tint = iconTint)
        }
    }
}

@Composable
private fun SunIcon(tint: Color) {
    Canvas(modifier = Modifier.size(22.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension * 0.22f
        drawCircle(color = tint, radius = radius, center = center)

        val rayStart = size.minDimension * 0.34f
        val rayEnd = size.minDimension * 0.48f
        val strokeWidth = size.minDimension * 0.08f
        for (i in 0 until 8) {
            val angle = Math.toRadians(i * 45.0)
            val dx = cos(angle).toFloat()
            val dy = sin(angle).toFloat()
            drawLine(
                color = tint,
                start = Offset(center.x + dx * rayStart, center.y + dy * rayStart),
                end = Offset(center.x + dx * rayEnd, center.y + dy * rayEnd),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
private fun MoonIcon(tint: Color) {
    Canvas(modifier = Modifier.size(22.dp)) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            fillType = PathFillType.EvenOdd
            addOval(Rect(center = Offset(w * 0.50f, h * 0.50f), radius = w * 0.34f))
            addOval(Rect(center = Offset(w * 0.68f, h * 0.38f), radius = w * 0.28f))
        }
        drawPath(path = path, color = tint)
    }
}
