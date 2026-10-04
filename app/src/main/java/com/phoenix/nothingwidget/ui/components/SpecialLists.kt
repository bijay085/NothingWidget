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
fun InstalledWidgetsScreen(
    widgets: List<WidgetItem>,
    favoriteIds: Set<String>,
    onBack: () -> Unit,
    onFavoriteClick: (String) -> Unit,
    onAddToHomeClick: (WidgetItem) -> Unit,
    onCustomizeClick: (WidgetItem) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    SpecialDestinationScaffold(
        title = "Installed Widgets",
        widgets = widgets,
        favoriteIds = favoriteIds,
        emptyTitle = "Nothing on your home screen",
        emptyMessage = "Widgets you place on the home screen will show up here.",
        onBack = onBack,
        onFavoriteClick = onFavoriteClick,
        onAddToHomeClick = onAddToHomeClick,
        onCustomizeClick = onCustomizeClick,
        modifier = modifier,
        isLoading = isLoading,
    )
}

@Composable
fun NewlyIntroducedScreen(
    widgets: List<WidgetItem>,
    favoriteIds: Set<String>,
    onBack: () -> Unit,
    onFavoriteClick: (String) -> Unit,
    onAddToHomeClick: (WidgetItem) -> Unit,
    onCustomizeClick: (WidgetItem) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    SpecialDestinationScaffold(
        title = "Newly Introduced",
        widgets = widgets,
        favoriteIds = favoriteIds,
        emptyTitle = "Nothing new",
        emptyMessage = "Widgets introduced in the last 10 days will show up here.",
        onBack = onBack,
        onFavoriteClick = onFavoriteClick,
        onAddToHomeClick = onAddToHomeClick,
        onCustomizeClick = onCustomizeClick,
        modifier = modifier,
        isLoading = isLoading,
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
    isLoading: Boolean = false,
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
        isLoading = isLoading,
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
    isLoading: Boolean = false,
) {
    val dimensions = AppTheme.dimensions

    Column(modifier = modifier.fillMaxWidth()) {
        BackButton(onClick = onBack)
        Spacer(modifier = Modifier.height(dimensions.medium))
        WidgetList(
            title = title,
            widgets = widgets,
            favoriteIds = favoriteIds,
            emptyTitle = emptyTitle,
            emptyMessage = emptyMessage,
            onFavoriteClick = onFavoriteClick,
            onAddToHomeClick = onAddToHomeClick,
            onCustomizeClick = onCustomizeClick,
            isLoading = isLoading,
            skeletonCount = 3,
        )
    }
}
