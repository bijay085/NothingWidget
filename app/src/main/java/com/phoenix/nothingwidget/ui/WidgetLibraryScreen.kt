package com.phoenix.nothingwidget.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.phoenix.nothingwidget.R
import com.phoenix.nothingwidget.ui.components.BackgroundPattern
import com.phoenix.nothingwidget.ui.components.CategoryFilter
import com.phoenix.nothingwidget.ui.components.SectionNavCard
import com.phoenix.nothingwidget.ui.components.ThemeToggleIcon
import com.phoenix.nothingwidget.ui.components.WidgetList
import com.phoenix.nothingwidget.ui.model.LibraryFilter
import com.phoenix.nothingwidget.ui.model.WidgetItem
import com.phoenix.nothingwidget.ui.theme.AppTheme
import com.phoenix.nothingwidget.ui.theme.AppThemeMode

@Composable
fun WidgetLibraryScreen(
    themeMode: AppThemeMode,
    onThemeModeChange: (AppThemeMode) -> Unit,
    catalog: List<WidgetItem>,
    favoriteIds: Set<String>,
    onFavoriteClick: (String) -> Unit,
    onAddToHomeClick: (WidgetItem) -> Unit,
    onCustomizeClick: (WidgetItem) -> Unit,
    recentCount: Int,
    favoriteCount: Int,
    onOpenRecentlyAdded: () -> Unit,
    onOpenFavorites: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val colors = AppTheme.colors
    val dimensions = AppTheme.dimensions
    val filters = remember(catalog) { LibraryFilter.tabsFor(catalog) }
    var selectedFilter by remember { mutableStateOf<LibraryFilter>(LibraryFilter.All) }

    val categoryWidgets = remember(selectedFilter, catalog) {
        when (val filter = selectedFilter) {
            LibraryFilter.All -> catalog
            is LibraryFilter.Category -> catalog.filter { it.category == filter.category }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(colors.background, colors.backgroundMuted),
                ),
            ),
    ) {
        BackgroundPattern()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
        ) {
            LibraryHeader(
                themeMode = themeMode,
                onThemeModeChange = onThemeModeChange,
                onOpenSettings = onOpenSettings,
            )

            Spacer(modifier = Modifier.height(dimensions.headerToTabs))

            CategoryFilter(
                filters = filters,
                selected = selectedFilter,
                onSelected = { selectedFilter = it },
            )

            Spacer(modifier = Modifier.height(dimensions.tabsToContent))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = dimensions.large)
                    .padding(bottom = dimensions.large + dimensions.medium),
                verticalArrangement = Arrangement.spacedBy(dimensions.itemSpacing),
            ) {
                when (val filter = selectedFilter) {
                    LibraryFilter.All -> {
                        SectionNavCard(
                            title = "Recently Added",
                            count = recentCount,
                            emptyLabel = "No recent widgets",
                            onClick = onOpenRecentlyAdded,
                        )

                        SectionNavCard(
                            title = "Favorites",
                            count = favoriteCount,
                            emptyLabel = "No favorites",
                            onClick = onOpenFavorites,
                        )

                        WidgetList(
                            title = "All Widgets",
                            widgets = categoryWidgets,
                            favoriteIds = favoriteIds,
                            emptyTitle = "No widgets",
                            emptyMessage = "Your widget library is empty.",
                            onFavoriteClick = onFavoriteClick,
                            onAddToHomeClick = onAddToHomeClick,
                            onCustomizeClick = onCustomizeClick,
                        )
                    }

                    is LibraryFilter.Category -> {
                        WidgetList(
                            title = filter.label,
                            widgets = categoryWidgets,
                            favoriteIds = favoriteIds,
                            emptyTitle = "No widgets",
                            emptyMessage = "No ${filter.label.lowercase()} widgets available.",
                            onFavoriteClick = onFavoriteClick,
                            onAddToHomeClick = onAddToHomeClick,
                            onCustomizeClick = onCustomizeClick,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LibraryHeader(
    themeMode: AppThemeMode,
    onThemeModeChange: (AppThemeMode) -> Unit,
    onOpenSettings: () -> Unit,
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val dimensions = AppTheme.dimensions
    val iconSize = 48.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.large)
            .padding(top = dimensions.large, bottom = dimensions.small),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_nothing_widget_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(iconSize)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surface),
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = "Nothing Widget",
                style = typography.title,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier.size(44.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = colors.textPrimary,
                    modifier = Modifier.size(22.dp),
                )
            }
            ThemeToggleIcon(
                themeMode = themeMode,
                onToggle = {
                    val next = when (themeMode) {
                        AppThemeMode.NOTHING_PREMIUM -> AppThemeMode.STUDIO_CLEAN
                        AppThemeMode.STUDIO_CLEAN -> AppThemeMode.NOTHING_PREMIUM
                    }
                    onThemeModeChange(next)
                },
            )
        }

        Spacer(modifier = Modifier.height(dimensions.small))

        Text(
            text = "Browse, favorite, and add widgets",
            style = typography.subtitle,
            color = colors.textSecondary,
            modifier = Modifier.padding(start = iconSize + 14.dp),
        )
    }
}
