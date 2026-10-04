package com.phoenix.nothingwidget.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.phoenix.nothingwidget.ui.theme.AppTheme

/**
 * Ambient background-only pattern. Sits behind content; never interactive.
 */
@Composable
fun BackgroundPattern(modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
    val orbA = if (colors.isDark) 0.20f else 0.09f
    val orbB = if (colors.isDark) 0.14f else 0.07f
    val waveA = if (colors.isDark) 0.10f else 0.05f
    val ringA = if (colors.isDark) 0.12f else 0.06f
    val dotA = if (colors.isDark) 0.12f else 0.06f

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    colors.patternPrimary.copy(alpha = orbA),
                    colors.patternPrimary.copy(alpha = 0f),
                ),
                center = Offset(w * 0.92f, h * 0.04f),
                radius = w * 0.70f,
            ),
            radius = w * 0.70f,
            center = Offset(w * 0.92f, h * 0.04f),
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    colors.patternSecondary.copy(alpha = orbB),
                    colors.patternSecondary.copy(alpha = 0f),
                ),
                center = Offset(w * -0.08f, h * 0.38f),
                radius = w * 0.62f,
            ),
            radius = w * 0.62f,
            center = Offset(w * -0.08f, h * 0.38f),
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    colors.patternPrimary.copy(alpha = orbA * 0.75f),
                    colors.patternPrimary.copy(alpha = 0f),
                ),
                center = Offset(w * 0.55f, h * 0.95f),
                radius = w * 0.58f,
            ),
            radius = w * 0.58f,
            center = Offset(w * 0.55f, h * 0.95f),
        )

        // Soft ring accents
        drawCircle(
            color = colors.patternSecondary.copy(alpha = ringA),
            radius = w * 0.28f,
            center = Offset(w * 0.18f, h * 0.18f),
            style = Stroke(width = 1.5f),
        )
        drawCircle(
            color = colors.patternPrimary.copy(alpha = ringA * 0.8f),
            radius = w * 0.42f,
            center = Offset(w * 0.85f, h * 0.55f),
            style = Stroke(width = 1.2f),
        )

        // Abstract waves
        val waveTop = Path().apply {
            moveTo(0f, h * 0.58f)
            cubicTo(w * 0.22f, h * 0.50f, w * 0.38f, h * 0.68f, w * 0.58f, h * 0.60f)
            cubicTo(w * 0.78f, h * 0.52f, w * 0.90f, h * 0.64f, w, h * 0.58f)
            lineTo(w, h * 0.66f)
            cubicTo(w * 0.88f, h * 0.70f, w * 0.72f, h * 0.58f, w * 0.52f, h * 0.66f)
            cubicTo(w * 0.32f, h * 0.74f, w * 0.16f, h * 0.60f, 0f, h * 0.68f)
            close()
        }
        drawPath(
            path = waveTop,
            color = colors.patternSecondary.copy(alpha = waveA),
            style = Fill,
        )

        val waveBottom = Path().apply {
            moveTo(0f, h * 0.82f)
            cubicTo(w * 0.30f, h * 0.74f, w * 0.48f, h * 0.90f, w * 0.70f, h * 0.84f)
            cubicTo(w * 0.88f, h * 0.80f, w * 0.96f, h * 0.88f, w, h * 0.86f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(
            path = waveBottom,
            color = colors.patternPrimary.copy(alpha = waveA * 0.85f),
            style = Fill,
        )

        // Dot field
        val stepX = w / 9f
        val stepY = h / 16f
        var row = 0
        var y = stepY * 1.5f
        while (y < h * 0.48f) {
            var x = if (row % 2 == 0) stepX * 0.4f else stepX * 0.9f
            while (x < w) {
                drawCircle(
                    color = colors.patternPrimary.copy(alpha = dotA),
                    radius = 2.1f,
                    center = Offset(x, y),
                )
                x += stepX
            }
            y += stepY
            row++
        }
    }
}
