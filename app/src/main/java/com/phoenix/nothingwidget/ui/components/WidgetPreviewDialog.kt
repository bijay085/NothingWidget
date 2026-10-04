package com.phoenix.nothingwidget.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.phoenix.nothingwidget.ui.theme.AppTheme

@Composable
fun WidgetPreviewDialog(
    widgetName: String,
    @DrawableRes previewResId: Int,
    onDismiss: () -> Unit,
    circular: Boolean = false,
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val dimensions = AppTheme.dimensions
    val previewShape: Shape = if (circular) CircleShape else RoundedCornerShape(24.dp)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensions.large),
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "✕",
                    style = typography.subtitle,
                    color = colors.textPrimary,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .clickable(onClick = onDismiss)
                        .padding(dimensions.small),
                )
            }

            Text(
                text = widgetName,
                style = typography.section,
                color = colors.textPrimary,
            )

            Spacer(modifier = Modifier.height(dimensions.medium))

            // Same single-layer rule as the thumbnail: clip + image only.
            Image(
                painter = painterResource(id = previewResId),
                contentDescription = "$widgetName preview",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(280.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = previewShape,
                        clip = false,
                        ambientColor = Color.Black.copy(alpha = 0.22f),
                        spotColor = Color.Black.copy(alpha = 0.28f),
                    )
                    .clip(previewShape),
            )
        }
    }
}
