package com.phoenix.nothingwidget.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.phoenix.nothingwidget.ui.theme.AppTheme

/** Soft shimmer bone used by every skeleton surface. */
@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(10.dp),
) {
    val colors = AppTheme.colors
    val base = if (colors.isDark) {
        colors.surfaceVariant.copy(alpha = 0.55f)
    } else {
        colors.surfaceVariant.copy(alpha = 0.85f)
    }
    val highlight = if (colors.isDark) {
        colors.textMuted.copy(alpha = 0.22f)
    } else {
        colors.card.copy(alpha = 0.95f)
    }

    val transition = rememberInfiniteTransition(label = "skeleton")
    val shift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "skeletonShift",
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(base, highlight, base),
                    start = Offset(shift * 420f - 180f, 0f),
                    end = Offset(shift * 420f + 180f, 220f),
                ),
            ),
    )
}

@Composable
fun WidgetPreviewSkeleton(
    modifier: Modifier = Modifier,
    size: Dp = AppTheme.dimensions.previewSize,
    circular: Boolean = false,
) {
    val displaySize = size + 16.dp
    val shape: Shape = if (circular) CircleShape else RoundedCornerShape(20.dp)
    SkeletonBox(
        modifier = modifier.size(displaySize),
        shape = shape,
    )
}

/** Card-shaped placeholder matching [WidgetCard] layout. */
@Composable
fun WidgetCardSkeleton(
    modifier: Modifier = Modifier,
    circularPreview: Boolean = false,
) {
    val colors = AppTheme.colors
    val dimensions = AppTheme.dimensions
    val cardShape = RoundedCornerShape(dimensions.cornerRadius)
    val elevation = if (colors.isDark) 5.dp else 8.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = elevation,
                shape = cardShape,
                ambientColor = colors.shadow.copy(alpha = if (colors.isDark) 0.20f else 0.10f),
                spotColor = colors.shadow.copy(alpha = if (colors.isDark) 0.26f else 0.12f),
            )
            .clip(cardShape)
            .background(colors.card)
            .cardDecoration()
            .border(1.dp, colors.cardBorder, cardShape)
            .padding(dimensions.cardPadding),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            WidgetPreviewSkeleton(
                size = dimensions.previewSize,
                circular = circularPreview,
            )
            Spacer(modifier = Modifier.width(dimensions.medium))
            Column(modifier = Modifier.weight(1f)) {
                SkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth(0.62f)
                        .height(18.dp),
                    shape = RoundedCornerShape(6.dp),
                )
                Spacer(modifier = Modifier.height(10.dp))
                SkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .height(12.dp),
                    shape = RoundedCornerShape(6.dp),
                )
                Spacer(modifier = Modifier.height(6.dp))
                SkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .height(12.dp),
                    shape = RoundedCornerShape(6.dp),
                )
                Spacer(modifier = Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SkeletonBox(
                        modifier = Modifier
                            .width(56.dp)
                            .height(22.dp),
                        shape = RoundedCornerShape(999.dp),
                    )
                    SkeletonBox(
                        modifier = Modifier
                            .width(48.dp)
                            .height(22.dp),
                        shape = RoundedCornerShape(999.dp),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SkeletonBox(
                modifier = Modifier
                    .weight(1f)
                    .height(dimensions.buttonHeight),
                shape = RoundedCornerShape(14.dp),
            )
            SkeletonBox(
                modifier = Modifier
                    .weight(1f)
                    .height(dimensions.buttonHeight),
                shape = RoundedCornerShape(14.dp),
            )
        }
    }
}

@Composable
fun WidgetListSkeleton(
    title: String? = "Widgets",
    count: Int = 3,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val dimensions = AppTheme.dimensions

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensions.itemSpacing),
    ) {
        if (title != null) {
            Text(
                text = title,
                style = typography.section,
                color = colors.textPrimary,
            )
        }
        repeat(count) { index ->
            WidgetCardSkeleton(circularPreview = index == 0)
        }
    }
}

/** Customize-screen placeholder while DataStore config loads. */
@Composable
fun CustomizationScreenSkeleton(
    widgetName: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val dimensions = AppTheme.dimensions
    val shape = RoundedCornerShape(20.dp)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensions.medium),
    ) {
        BackButton(onClick = onBack)
        Text(
            text = "Customize $widgetName",
            style = typography.title,
            color = colors.textPrimary,
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(shape)
                .background(colors.previewSurface)
                .border(1.dp, colors.previewBorder, shape),
        ) {
            WidgetPreviewSkeleton(size = 140.dp)
        }

        repeat(3) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.card)
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                    .padding(dimensions.cardPadding),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(14.dp),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(4) {
                        SkeletonBox(
                            modifier = Modifier.size(36.dp),
                            shape = CircleShape,
                        )
                    }
                }
            }
        }
    }
}
