package com.phoenix.nothingwidget.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.phoenix.nothingwidget.ui.model.WidgetItem
import com.phoenix.nothingwidget.ui.theme.AppTheme

@Composable
fun RecentlyAddedScreen(
    widgets: List<WidgetItem>,
    favoriteIds: Set<String>,
    onBack: () -> Unit,
    onFavoriteClick: (String) -> Unit,
    onAddToHomeClick: (WidgetItem) -> Unit,
    onCustomizeClick: (WidgetItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    SpecialDestinationScaffold(
        title = "Recently Added",
        widgets = widgets,
        favoriteIds = favoriteIds,
        emptyTitle = "Nothing recent",
        emptyMessage = "Widgets added in the last 10 days will show up here.",
        onBack = onBack,
        onFavoriteClick = onFavoriteClick,
        onAddToHomeClick = onAddToHomeClick,
        onCustomizeClick = onCustomizeClick,
        modifier = modifier,
    )
}

@Composable
fun FavoritesScreen(
    widgets: List<WidgetItem>,
    favoriteIds: Set<String>,
    onBack: () -> Unit,
    onFavoriteClick: (String) -> Unit,
    onAddToHomeClick: (WidgetItem) -> Unit,
    onCustomizeClick: (WidgetItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    SpecialDestinationScaffold(
        title = "Favorites",
        widgets = widgets,
        favoriteIds = favoriteIds,
        emptyTitle = "No favorites yet",
        emptyMessage = "Tap the heart on a widget card to save it here.",
        onBack = onBack,
        onFavoriteClick = onFavoriteClick,
        onAddToHomeClick = onAddToHomeClick,
        onCustomizeClick = onCustomizeClick,
        modifier = modifier,
    )
}

@Composable
private fun SpecialDestinationScaffold(
    title: String,
    widgets: List<WidgetItem>,
    favoriteIds: Set<String>,
    emptyTitle: String,
    emptyMessage: String,
    onBack: () -> Unit,
    onFavoriteClick: (String) -> Unit,
    onAddToHomeClick: (WidgetItem) -> Unit,
    onCustomizeClick: (WidgetItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val dimensions = AppTheme.dimensions

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "‹ Back",
            style = typography.subtitle,
            color = colors.primary,
            modifier = Modifier
                .clickable(onClick = onBack)
                .padding(vertical = dimensions.small / 2),
        )
        Spacer(modifier = Modifier.height(dimensions.small))
        WidgetList(
            title = title,
            widgets = widgets,
            favoriteIds = favoriteIds,
            emptyTitle = emptyTitle,
            emptyMessage = emptyMessage,
            onFavoriteClick = onFavoriteClick,
            onAddToHomeClick = onAddToHomeClick,
            onCustomizeClick = onCustomizeClick,
        )
    }
}
