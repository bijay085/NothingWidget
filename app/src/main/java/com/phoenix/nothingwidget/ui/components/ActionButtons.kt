package com.phoenix.nothingwidget.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.phoenix.nothingwidget.ui.theme.AppTheme

private val ButtonShape = RoundedCornerShape(16.dp)

@Composable
private fun Modifier.pressScale(pressed: Boolean): Modifier {
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(),
        label = "buttonPressScale",
    )
    return this.scale(scale)
}

/** Filled call-to-action button (Add to Home Screen, Apply). */
@Composable
fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = AppTheme.colors
    val dimensions = AppTheme.dimensions
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        modifier = modifier
            .height(dimensions.buttonHeight)
            .pressScale(pressed),
        shape = ButtonShape,
        contentPadding = PaddingValues(horizontal = 12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.primary,
            contentColor = colors.onPrimary,
            disabledContainerColor = colors.primary.copy(alpha = 0.35f),
            disabledContentColor = colors.onPrimary.copy(alpha = 0.6f),
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (colors.isDark) 0.dp else 2.dp,
            pressedElevation = 0.dp,
        ),
    ) {
        Text(
            text = text,
            style = AppTheme.typography.button,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Tinted outline button used for the secondary action on a card or screen. */
@Composable
fun SecondaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Boolean = true,
) {
    val colors = AppTheme.colors
    val dimensions = AppTheme.dimensions
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val contentColor = if (accent) colors.primary else colors.textSecondary
    val borderColor = if (accent) colors.primary.copy(alpha = 0.45f) else colors.divider

    OutlinedButton(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier
            .height(dimensions.buttonHeight)
            .pressScale(pressed),
        shape = ButtonShape,
        border = BorderStroke(1.dp, borderColor),
        contentPadding = PaddingValues(horizontal = 12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (accent) colors.primary.copy(alpha = 0.08f) else colors.surfaceVariant,
            contentColor = contentColor,
        ),
    ) {
        Text(
            text = text,
            style = AppTheme.typography.button,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
