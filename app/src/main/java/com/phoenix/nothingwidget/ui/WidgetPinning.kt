package com.phoenix.nothingwidget.ui

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.widget.Toast
import com.phoenix.nothingwidget.ui.data.WidgetCatalog
import com.phoenix.nothingwidget.ui.model.WidgetItem
import com.phoenix.nothingwidget.widgets.screen_time.ScreenTimeRepository
import com.phoenix.nothingwidget.widgets.screen_time.ScreenTimeWidgetReceiver
import com.phoenix.nothingwidget.widgets.screen_time_large.ScreenTimeLargeWidgetReceiver
import com.phoenix.nothingwidget.widgets.weather.WeatherLocationHelper
import com.phoenix.nothingwidget.widgets.weather.WeatherWidgetReceiver

/** Number of placed home-screen instances per widget id (only ids with at least one). */
fun installedWidgetCounts(context: Context, catalog: List<WidgetItem>): Map<String, Int> {
    val manager = AppWidgetManager.getInstance(context)
    return catalog.associate { widget ->
        val provider = ComponentName(context.packageName, widget.providerClassName)
        widget.id to manager.getAppWidgetIds(provider).size
    }.filterValues { it > 0 }
}

/**
 * App-side "Add to Home Screen" using [AppWidgetManager.requestPinAppWidget].
 */
fun requestAddWidgetToHome(
    context: Context,
    widget: WidgetItem,
    requestLocationPermission: (() -> Unit)? = null,
    requestUsageAccess: (() -> Unit)? = null,
) {
    if (widget.id == WidgetCatalog.ID_WEATHER && !WeatherLocationHelper.hasLocationPermission(context)) {
        requestLocationPermission?.invoke()
        Toast.makeText(
            context,
            "Location permission helps show your city on Weather.",
            Toast.LENGTH_SHORT,
        ).show()
    }

    val needsUsageAccess = widget.id == WidgetCatalog.ID_SCREEN_TIME ||
        widget.id == WidgetCatalog.ID_SCREEN_TIME_LARGE
    if (needsUsageAccess && !ScreenTimeRepository.hasUsageAccess(context)) {
        requestUsageAccess?.invoke()
    }

    val manager = AppWidgetManager.getInstance(context)
    if (!manager.isRequestPinAppWidgetSupported) {
        Toast.makeText(
            context,
            "Pinning isn’t supported here. Long-press the home screen → Widgets → Nothing Widget.",
            Toast.LENGTH_LONG,
        ).show()
        return
    }

    val provider = ComponentName(context.packageName, widget.providerClassName)
    val accepted = manager.requestPinAppWidget(provider, null, null)
    if (!accepted) {
        Toast.makeText(
            context,
            "Couldn’t start pin request. Try Widgets from the home screen.",
            Toast.LENGTH_LONG,
        ).show()
        return
    }

    if (widget.id == WidgetCatalog.ID_WEATHER) {
        // Always re-capture after pin so DataStore city is written into RemoteViews.
        WeatherWidgetReceiver.captureLocationThenRefresh(context)
    }

    if (widget.id == WidgetCatalog.ID_SCREEN_TIME) {
        ScreenTimeWidgetReceiver.requestRefresh(context)
    }
    if (widget.id == WidgetCatalog.ID_SCREEN_TIME_LARGE) {
        ScreenTimeLargeWidgetReceiver.requestRefresh(context)
    }
}
