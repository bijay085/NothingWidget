package com.phoenix.nothingwidget.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.phoenix.nothingwidget.ui.theme.AppTheme

/**
 * Subtle surface structure for app cards: a glowing top edge, a soft top glow,
 * and a fading corner dot grid.
 * Apply after `.background(...)` so it sits above the fill and below content.
 */
@Composable
fun Modifier.cardDecoration(): Modifier {
    val colors = AppTheme.colors
    val accent = colors.patternPrimary
    val glowAlpha = if (colors.isDark) 0.10f else 0.035f
    val edgeAlpha = if (colors.isDark) 0.55f else 0.40f
    val dotAlpha = if (colors.isDark) 0.22f else 0.18f

    return this.drawBehind {
        val w = size.width
        val h = size.height
        val unit = 6.dp.toPx()

        // Soft glow washing down from the top edge
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(accent.copy(alpha = glowAlpha), Color.Transparent),
                startY = 0f,
                endY = h * 0.55f,
            ),
            size = Size(w, h * 0.55f),
        )

        // Glowing top edge, brightest in the middle
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    accent.copy(alpha = edgeAlpha),
                    Color.Transparent,
                ),
            ),
            size = Size(w, 1.5.dp.toPx()),
        )

        // Fading dot grid in the top-right corner
        val cols = 6
        val rows = 4
        val gap = unit * 1.6f
        val originX = w - unit * 2f - gap * (cols - 1)
        val originY = unit * 2f
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val fade = ((c + 1f) / cols) * (1f - r / rows.toFloat())
                drawCircle(
                    color = accent.copy(alpha = dotAlpha * fade),
                    radius = 1.dp.toPx(),
                    center = Offset(originX + c * gap, originY + r * gap),
                )
            }
        }
    }
}
