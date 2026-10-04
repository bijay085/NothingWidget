package com.phoenix.nothingwidget.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.phoenix.nothingwidget.ui.theme.AppTheme

@Composable
fun WidgetCard(
    name: String,
    category: String,
    tags: List<String>,
    isFavorite: Boolean,
    @DrawableRes previewResId: Int,
    previewCircular: Boolean = false,
    onFavoriteClick: () -> Unit,
    onAddToHomeClick: () -> Unit,
    modifier: Modifier = Modifier,
    onCustomizeClick: (() -> Unit)? = null,
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val dimensions = AppTheme.dimensions
    val cardShape = RoundedCornerShape(dimensions.cornerRadius)
    val elevation = if (colors.isDark) 5.dp else 8.dp
    var showPreviewDialog by remember { mutableStateOf(false) }

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
            .border(1.dp, colors.cardBorder, cardShape)
            .padding(dimensions.cardPadding),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            WidgetPreviewThumbnail(
                previewResId = previewResId,
                contentDescription = "$name preview",
                size = dimensions.previewSize,
                circular = previewCircular,
                onClick = { showPreviewDialog = true },
            )

            Spacer(modifier = Modifier.width(dimensions.medium))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name,
                        style = typography.cardTitle,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(dimensions.iconButton)
                            .clip(CircleShape)
                            .clickable(onClick = onFavoriteClick),
                    ) {
                        Text(
                            text = if (isFavorite) "♥" else "♡",
                            style = typography.cardTitle,
                            color = if (isFavorite) colors.primary else colors.textMuted,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = category,
                    style = typography.category,
                    color = colors.textSecondary,
                )

                Spacer(modifier = Modifier.height(12.dp))

                WidgetTagRow(tags = tags)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (onCustomizeClick != null) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SecondaryActionButton(
                    text = "Customize",
                    onClick = onCustomizeClick,
                    modifier = Modifier.weight(1f),
                )
                PrimaryActionButton(
                    text = "Add to Home",
                    onClick = onAddToHomeClick,
                    modifier = Modifier.weight(1f),
                )
            }
        } else {
            PrimaryActionButton(
                text = "Add to Home Screen",
                onClick = onAddToHomeClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    if (showPreviewDialog) {
        WidgetPreviewDialog(
            widgetName = name,
            previewResId = previewResId,
            circular = previewCircular,
            onDismiss = { showPreviewDialog = false },
        )
    }
}
