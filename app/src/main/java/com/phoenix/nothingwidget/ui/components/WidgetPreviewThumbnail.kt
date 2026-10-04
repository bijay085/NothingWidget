package com.phoenix.nothingwidget.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Single-layer widget preview.
 *
 * Correct: [Preview container] → [Widget image]
 * Wrong: nested card / surface / image box / widget stack.
 */
@Composable
fun WidgetPreviewThumbnail(
    @DrawableRes previewResId: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    circular: Boolean = false,
) {
    val displaySize = size + 16.dp
    val shape: Shape = if (circular) {
        CircleShape
    } else {
        RoundedCornerShape(20.dp)
    }

    Image(
        painter = painterResource(id = previewResId),
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .size(displaySize)
            .shadow(
                elevation = 4.dp,
                shape = shape,
                clip = false,
                ambientColor = Color.Black.copy(alpha = 0.18f),
                spotColor = Color.Black.copy(alpha = 0.22f),
            )
            .clip(shape)
            .clickable(onClick = onClick),
    )
}
