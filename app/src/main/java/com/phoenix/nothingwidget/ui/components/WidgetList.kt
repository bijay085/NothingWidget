package com.phoenix.nothingwidget.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.phoenix.nothingwidget.ui.model.WidgetItem
import com.phoenix.nothingwidget.ui.theme.AppTheme

@Composable
fun WidgetList(
    title: String,
    widgets: List<WidgetItem>,
    favoriteIds: Set<String>,
    emptyTitle: String,
    emptyMessage: String,
    onFavoriteClick: (String) -> Unit,
    onAddToHomeClick: (WidgetItem) -> Unit,
    onCustomizeClick: (WidgetItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val dimensions = AppTheme.dimensions

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensions.itemSpacing),
    ) {
        Text(
            text = title,
            style = typography.section,
            color = colors.textPrimary,
        )

        if (widgets.isEmpty()) {
            EmptyState(
                title = emptyTitle,
                message = emptyMessage,
            )
        } else {
            widgets.forEach { widget ->
                WidgetCard(
                    name = widget.name,
                    category = widget.category.label,
                    tags = widget.resolvedTags(),
                    isFavorite = widget.id in favoriteIds,
                    previewResId = widget.previewResId,
                    previewCircular = widget.previewCircular,
                    onFavoriteClick = { onFavoriteClick(widget.id) },
                    onAddToHomeClick = { onAddToHomeClick(widget) },
                    onCustomizeClick = widget.customization?.let { { onCustomizeClick(widget) } },
                )
            }
        }
    }
}
