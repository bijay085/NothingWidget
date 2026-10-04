package com.phoenix.nothingwidget.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.phoenix.nothingwidget.ui.theme.AppTheme

/**
 * Ambient background-only pattern: top glow, graph grid, crosshair marks at
 * intersections, a repeating square/dot motif, and two glowing accent lines.
 * Sits behind content; never interactive.
 */
@Composable
fun BackgroundPattern(modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
    val dark = colors.isDark
    val primary = colors.patternPrimary
    val secondary = colors.patternSecondary

    val glowAlpha = if (dark) 0.16f else 0.10f
    val gridAlpha = if (dark) 0.06f else 0.07f
    val squareAlpha = if (dark) 0.12f else 0.13f
    val dotAlpha = if (dark) 0.16f else 0.16f
    val crossAlpha = if (dark) 0.22f else 0.22f
    val accentAlpha = if (dark) 0.28f else 0.22f
    val frameAlpha = if (dark) 0.20f else 0.18f

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val cell = w / 6f

        // Top glow band
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(primary.copy(alpha = glowAlpha), Color.Transparent),
                startY = 0f,
                endY = h * 0.32f,
            ),
            size = Size(w, h * 0.32f),
        )

        // Graph grid
        var gx = cell
        while (gx < w) {
            drawLine(secondary.copy(alpha = gridAlpha), Offset(gx, 0f), Offset(gx, h), 1f)
            gx += cell
        }
        var gy = cell
        while (gy < h) {
            drawLine(secondary.copy(alpha = gridAlpha), Offset(0f, gy), Offset(w, gy), 1f)
            gy += cell
        }

        // Repeating motif inside cells
        val side = cell * 0.26f
        val inset = (cell - side) / 2f
        var row = 0
        var top = 0f
        while (top < h) {
            var col = 0
            var left = 0f
            while (left < w) {
                val center = Offset(left + cell / 2f, top + cell / 2f)
                when ((col + row) % 3) {
                    0 -> drawRoundRect(
                        color = primary.copy(alpha = squareAlpha),
                        topLeft = Offset(left + inset, top + inset),
                        size = Size(side, side),
                        cornerRadius = CornerRadius(4f, 4f),
                        style = Stroke(width = 1.2f),
                    )

                    1 -> drawCircle(
                        color = secondary.copy(alpha = dotAlpha),
                        radius = 2.2f,
                        center = center,
                    )

                    else -> Unit
                }
                left += cell
                col++
            }
            top += cell
            row++
        }

        // Crosshair marks on every other grid intersection
        val arm = cell * 0.07f
        var iy = cell
        var irow = 0
        while (iy < h) {
            var ix = if (irow % 2 == 0) cell else cell * 2f
            while (ix < w) {
                crosshair(Offset(ix, iy), arm, primary.copy(alpha = crossAlpha))
                ix += cell * 2f
            }
            iy += cell
            irow++
        }

        // Glowing diagonal accent lines (kept clear of the header text)
        glowLine(Offset(-cell, h * 0.62f), Offset(w * 0.45f, h * 0.40f), primary, accentAlpha)
        glowLine(Offset(w * 0.45f, h * 0.98f), Offset(w + cell, h * 0.72f), secondary, accentAlpha * 0.85f)
        glowLine(Offset(w * 0.60f, h * 0.30f), Offset(w + cell, h * 0.18f), secondary, accentAlpha * 0.8f)
        glowLine(Offset(-cell, h * 0.90f), Offset(w * 0.30f, h * 0.78f), primary, accentAlpha * 0.7f)
        glowLine(Offset(w * 0.20f, h * 0.52f), Offset(w * 0.70f, h * 0.66f), primary, accentAlpha * 0.5f)

        // Short grid-aligned accent segments
        glowLine(Offset(cell * 4f, h * 0.36f), Offset(cell * 6f, h * 0.36f), primary, accentAlpha * 0.7f)
        glowLine(Offset(0f, h * 0.70f), Offset(cell * 2f, h * 0.70f), secondary, accentAlpha * 0.7f)
        glowLine(Offset(cell, h * 0.44f), Offset(cell, h * 0.56f), primary, accentAlpha * 0.6f)
        glowLine(Offset(cell * 5f, h * 0.80f), Offset(cell * 5f, h * 0.92f), secondary, accentAlpha * 0.6f)

        // Nested square frames
        squareFrame(Offset(w * 0.86f, h * 0.48f), cell * 0.9f, primary, frameAlpha)
        squareFrame(Offset(w * 0.12f, h * 0.84f), cell * 0.7f, secondary, frameAlpha)
        squareFrame(Offset(w * 0.55f, h * 0.88f), cell * 0.5f, primary, frameAlpha * 0.8f)

        // Small triangles
        triangle(Offset(w * 0.30f, h * 0.34f), cell * 0.22f, secondary.copy(alpha = frameAlpha))
        triangle(Offset(w * 0.78f, h * 0.62f), cell * 0.18f, primary.copy(alpha = frameAlpha))
        triangle(Offset(w * 0.40f, h * 0.74f), cell * 0.20f, secondary.copy(alpha = frameAlpha))
    }
}

private fun DrawScope.squareFrame(center: Offset, half: Float, color: Color, alpha: Float) {
    listOf(1f, 0.62f, 0.28f).forEachIndexed { i, scale ->
        val s = half * scale
        drawRect(
            color = color.copy(alpha = alpha * (1f - i * 0.25f)),
            topLeft = Offset(center.x - s, center.y - s),
            size = Size(s * 2f, s * 2f),
            style = Stroke(width = 1.3f),
        )
    }
}

private fun DrawScope.triangle(center: Offset, r: Float, color: Color) {
    val path = Path().apply {
        moveTo(center.x, center.y - r)
        lineTo(center.x + r * 0.87f, center.y + r * 0.5f)
        lineTo(center.x - r * 0.87f, center.y + r * 0.5f)
        close()
    }
    drawPath(path, color, style = Stroke(width = 1.3f))
}

private fun DrawScope.crosshair(center: Offset, arm: Float, color: Color) {
    drawLine(color, Offset(center.x - arm, center.y), Offset(center.x + arm, center.y), 1.3f)
    drawLine(color, Offset(center.x, center.y - arm), Offset(center.x, center.y + arm), 1.3f)
}

/** A thin line with a soft halo drawn as wider, fainter passes underneath. */
private fun DrawScope.glowLine(start: Offset, end: Offset, color: Color, alpha: Float) {
    drawLine(color.copy(alpha = alpha * 0.12f), start, end, 18f, StrokeCap.Round)
    drawLine(color.copy(alpha = alpha * 0.25f), start, end, 8f, StrokeCap.Round)
    drawLine(color.copy(alpha = alpha), start, end, 1.6f, StrokeCap.Round)
}
