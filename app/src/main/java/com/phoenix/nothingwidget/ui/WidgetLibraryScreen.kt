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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.border
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.phoenix.nothingwidget.R
import com.phoenix.nothingwidget.ui.components.BackgroundPattern
import com.phoenix.nothingwidget.ui.components.CategoryFilter
import com.phoenix.nothingwidget.ui.components.SectionEntry
import com.phoenix.nothingwidget.ui.components.SectionGroupCard
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
    installedTypeCount: Int,
    installedInstanceCount: Int,
    recentCount: Int,
    favoriteCount: Int,
    isLoading: Boolean = false,
    onOpenInstalled: () -> Unit,
    onOpenNewlyIntroduced: () -> Unit,
    onOpenFavorites: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val colors = AppTheme.colors
    val dimensions = AppTheme.dimensions
    val filters = remember(catalog) { LibraryFilter.tabsFor(catalog) }
    var selectedFilter by remember { mutableStateOf<LibraryFilter>(LibraryFilter.All) }
    var query by rememberSaveable { mutableStateOf("") }
    val searching = query.isNotBlank()

    val categoryWidgets = remember(selectedFilter, catalog, query) {
        val inCategory = when (val filter = selectedFilter) {
            LibraryFilter.All -> catalog
            is LibraryFilter.Category -> catalog.filter { it.category == filter.category }
        }
        val q = query.trim()
        if (q.isEmpty()) {
            inCategory
        } else {
            inCategory.filter { widget ->
                widget.name.contains(q, ignoreCase = true) ||
                    widget.description.contains(q, ignoreCase = true) ||
                    widget.category.label.contains(q, ignoreCase = true) ||
                    widget.metadata.tags.any { it.contains(q, ignoreCase = true) }
            }
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

            SearchField(
                query = query,
                onQueryChange = { query = it },
                modifier = Modifier.padding(horizontal = dimensions.large),
            )

            Spacer(modifier = Modifier.height(dimensions.small + 4.dp))

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
                if (searching) {
                    WidgetList(
                        title = "Results",
                        widgets = categoryWidgets,
                        favoriteIds = favoriteIds,
                        emptyTitle = "No matches",
                        emptyMessage = "No widgets match \"${query.trim()}\".",
                        onFavoriteClick = onFavoriteClick,
                        onAddToHomeClick = onAddToHomeClick,
                        onCustomizeClick = onCustomizeClick,
                        isLoading = isLoading,
                        skeletonCount = 3,
                    )
                } else when (val filter = selectedFilter) {
                    LibraryFilter.All -> {
                        if (!isLoading) {
                            SectionGroupCard(
                                title = "My Widgets",
                                summary = "$installedTypeCount installed · $recentCount new · $favoriteCount favorites",
                                entries = listOf(
                                    SectionEntry(
                                        title = "Installed Widgets",
                                        subtitle = installedSubtitle(installedTypeCount, installedInstanceCount),
                                        muted = installedTypeCount == 0,
                                        onClick = onOpenInstalled,
                                    ),
                                    SectionEntry(
                                        title = "Newly Introduced",
                                        subtitle = countLabel(recentCount, "No new widgets"),
                                        muted = recentCount == 0,
                                        onClick = onOpenNewlyIntroduced,
                                    ),
                                    SectionEntry(
                                        title = "Favorites",
                                        subtitle = countLabel(favoriteCount, "No favorites"),
                                        muted = favoriteCount == 0,
                                        onClick = onOpenFavorites,
                                    ),
                                ),
                            )
                        }

                        WidgetList(
                            title = "All Widgets",
                            widgets = categoryWidgets,
                            favoriteIds = favoriteIds,
                            emptyTitle = "No widgets",
                            emptyMessage = "Your widget library is empty.",
                            onFavoriteClick = onFavoriteClick,
                            onAddToHomeClick = onAddToHomeClick,
                            onCustomizeClick = onCustomizeClick,
                            isLoading = isLoading,
                            skeletonCount = catalog.size.coerceIn(2, 4),
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
                            isLoading = isLoading,
                            skeletonCount = 3,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val shape = RoundedCornerShape(999.dp)
    var focused by remember { mutableStateOf(false) }
    val active = focused || query.isNotEmpty()
    val borderColor = if (active) colors.primary else colors.cardBorder
    val glowAlpha = if (colors.isDark) 0.55f else 0.35f

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp)
            .then(
                if (active) {
                    Modifier.shadow(
                        elevation = 14.dp,
                        shape = shape,
                        ambientColor = colors.primary.copy(alpha = glowAlpha),
                        spotColor = colors.primary.copy(alpha = glowAlpha),
                    )
                } else {
                    Modifier
                },
            )
            .clip(shape)
            .background(colors.card)
            .border(width = if (active) 1.5.dp else 1.dp, color = borderColor, shape = shape)
            .padding(start = 14.dp, end = 6.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = if (active) colors.primary else colors.textMuted,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(text = "Search widgets", style = typography.subtitle, color = colors.textMuted)
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = typography.subtitle.copy(color = colors.textPrimary),
                cursorBrush = SolidColor(colors.primary),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focused = it.isFocused },
            )
        }
        if (query.isNotEmpty()) {
            IconButton(
                onClick = { onQueryChange("") },
                modifier = Modifier.size(36.dp),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(colors.surfaceVariant),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear search",
                        tint = colors.textPrimary,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

private fun countLabel(count: Int, emptyLabel: String): String = when (count) {
    0 -> emptyLabel
    1 -> "1 widget"
    else -> "$count widgets"
}

private fun installedSubtitle(types: Int, instances: Int): String {
    if (types == 0) return "Nothing on your home screen"
    return "${countLabel(types, "")} · $instances on home screen"
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
