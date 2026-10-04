package com.phoenix.nothingwidget.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import com.phoenix.nothingwidget.ui.components.BackgroundPattern
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.phoenix.nothingwidget.ui.components.FavoritesScreen
import com.phoenix.nothingwidget.ui.components.InstalledWidgetsScreen
import com.phoenix.nothingwidget.ui.components.NewlyIntroducedScreen
import com.phoenix.nothingwidget.ui.customization.WidgetCustomizationScreen
import com.phoenix.nothingwidget.ui.data.FavoritesStore
import com.phoenix.nothingwidget.ui.data.WidgetCatalog
import com.phoenix.nothingwidget.ui.model.AppDestination
import com.phoenix.nothingwidget.ui.model.WidgetItem
import com.phoenix.nothingwidget.ui.settings.SettingsScreen
import com.phoenix.nothingwidget.ui.theme.AppTheme
import com.phoenix.nothingwidget.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    themeMode: AppThemeMode,
    onThemeModeChange: (AppThemeMode) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val catalog = remember(context) { WidgetCatalog.all(context) }
    var destination by remember { mutableStateOf<AppDestination>(AppDestination.Library) }
    val favoritesFlow = remember { FavoritesStore.favoritesFlow(context) }
    val favoriteIds by favoritesFlow.collectAsState(initial = emptySet())
    val requestWeatherLocationPermission = rememberWeatherLocationPermissionRequester()
    val requestScreenTimeUsageAccess = rememberScreenTimeUsageAccessRequester()
    WeatherLocationBootstrap()

    var favoritesReady by remember { mutableStateOf(false) }
    LaunchedEffect(favoritesFlow) {
        favoritesFlow.first()
        favoritesReady = true
    }

    var installedCounts by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var installedReady by remember { mutableStateOf(false) }
    LifecycleResumeEffect(catalog) {
        installedCounts = installedWidgetCounts(context, catalog)
        installedReady = true
        onPauseOrDispose { }
    }
    val widgetsLoading = !favoritesReady || !installedReady
    val installedWidgets = remember(catalog, installedCounts) {
        catalog.filter { it.id in installedCounts }
    }
    val recentWidgets = remember(catalog) { catalog.filter { it.isRecentlyAdded() } }
    val favoriteWidgets = remember(catalog, favoriteIds) {
        catalog.filter { it.id in favoriteIds }
    }
    val onFavoriteClick: (String) -> Unit = { id ->
        scope.launch { FavoritesStore.toggle(context, id) }
    }
    val onAddToHomeClick: (WidgetItem) -> Unit = { widget ->
        requestAddWidgetToHome(
            context = context,
            widget = widget,
            requestLocationPermission = requestWeatherLocationPermission,
            requestUsageAccess = requestScreenTimeUsageAccess,
        )
    }
    var customizeReturn by remember { mutableStateOf<AppDestination>(AppDestination.Library) }
    val onCustomizeClick: (WidgetItem) -> Unit = { widget ->
        customizeReturn = destination
        destination = AppDestination.Customize(widget.id)
    }

    BackHandler(
        enabled = destination != AppDestination.Library && destination !is AppDestination.Customize,
    ) {
        destination = AppDestination.Library
    }

    when (val current = destination) {
        AppDestination.Library -> {
            WidgetLibraryScreen(
                themeMode = themeMode,
                onThemeModeChange = onThemeModeChange,
                catalog = catalog,
                favoriteIds = favoriteIds,
                onFavoriteClick = onFavoriteClick,
                onAddToHomeClick = onAddToHomeClick,
                onCustomizeClick = onCustomizeClick,
                installedTypeCount = installedWidgets.size,
                installedInstanceCount = installedCounts.values.sum(),
                recentCount = recentWidgets.size,
                favoriteCount = favoriteWidgets.size,
                isLoading = widgetsLoading,
                onOpenInstalled = { destination = AppDestination.Installed },
                onOpenNewlyIntroduced = { destination = AppDestination.RecentlyAdded },
                onOpenFavorites = { destination = AppDestination.Favorites },
                onOpenSettings = { destination = AppDestination.Settings },
            )
        }

        AppDestination.Settings -> {
            SpecialDestinationHost {
                SettingsScreen(
                    onBack = { destination = AppDestination.Library },
                )
            }
        }

        AppDestination.Installed -> {
            SpecialDestinationHost {
                InstalledWidgetsScreen(
                    widgets = installedWidgets,
                    favoriteIds = favoriteIds,
                    onBack = { destination = AppDestination.Library },
                    onFavoriteClick = onFavoriteClick,
                    onAddToHomeClick = onAddToHomeClick,
                    onCustomizeClick = onCustomizeClick,
                    isLoading = widgetsLoading,
                )
            }
        }

        AppDestination.RecentlyAdded -> {
            SpecialDestinationHost {
                NewlyIntroducedScreen(
                    widgets = recentWidgets,
                    favoriteIds = favoriteIds,
                    onBack = { destination = AppDestination.Library },
                    onFavoriteClick = onFavoriteClick,
                    onAddToHomeClick = onAddToHomeClick,
                    onCustomizeClick = onCustomizeClick,
                    isLoading = widgetsLoading,
                )
            }
        }

        AppDestination.Favorites -> {
            SpecialDestinationHost {
                FavoritesScreen(
                    widgets = favoriteWidgets,
                    favoriteIds = favoriteIds,
                    onBack = { destination = AppDestination.Library },
                    onFavoriteClick = onFavoriteClick,
                    onAddToHomeClick = onAddToHomeClick,
                    onCustomizeClick = onCustomizeClick,
                    isLoading = widgetsLoading,
                )
            }
        }

        is AppDestination.Customize -> {
            val widget = catalog.firstOrNull { it.id == current.widgetId }
            val customization = widget?.customization
            if (widget == null || customization == null) {
                LaunchedEffect(current) { destination = AppDestination.Library }
            } else {
                val close = { destination = customizeReturn }
                BackHandler(onBack = close)
                SpecialDestinationHost(scrollable = false) {
                    WidgetCustomizationScreen(
                        widgetName = widget.name,
                        previewCircular = widget.previewCircular,
                        customization = customization,
                        onBack = close,
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecialDestinationHost(
    scrollable: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colors = AppTheme.colors
    val dimensions = AppTheme.dimensions

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
                .statusBarsPadding()
                .then(if (scrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier)
                .padding(horizontal = dimensions.large)
                .padding(
                    top = dimensions.large,
                    bottom = if (scrollable) dimensions.large + dimensions.medium else 0.dp,
                ),
        ) {
            content()
        }
    }
}
