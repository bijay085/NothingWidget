package com.phoenix.nothingwidget.ui.data

import android.content.Context
import com.phoenix.nothingwidget.R
import com.phoenix.nothingwidget.ui.model.WidgetCategory
import com.phoenix.nothingwidget.ui.model.WidgetItem
import com.phoenix.nothingwidget.widgets.round_clock.RoundClockConfig
import com.phoenix.nothingwidget.widgets.round_clock.RoundClockCustomization
import com.phoenix.nothingwidget.widgets.round_clock.RoundClockWidgetReceiver
import com.phoenix.nothingwidget.widgets.screen_time.ScreenTimeConfig
import com.phoenix.nothingwidget.widgets.screen_time.ScreenTimeCustomization
import com.phoenix.nothingwidget.widgets.screen_time.ScreenTimeWidgetReceiver
import com.phoenix.nothingwidget.widgets.screen_time_large.ScreenTimeLargeConfig
import com.phoenix.nothingwidget.widgets.screen_time_large.ScreenTimeLargeCustomization
import com.phoenix.nothingwidget.widgets.screen_time_large.ScreenTimeLargeWidgetReceiver
import com.phoenix.nothingwidget.widgets.quick_actions.QuickActionsConfig
import com.phoenix.nothingwidget.widgets.quick_actions.QuickActionsCustomization
import com.phoenix.nothingwidget.widgets.quick_actions.QuickActionsWidgetReceiver
import com.phoenix.nothingwidget.widgets.weather.WeatherConfig
import com.phoenix.nothingwidget.widgets.weather.WeatherCustomization
import com.phoenix.nothingwidget.widgets.weather.WeatherWidgetReceiver

object WidgetCatalog {
    const val ID_ROUND_CLOCK = RoundClockConfig.WIDGET_ID
    const val ID_WEATHER = WeatherConfig.WIDGET_ID
    const val ID_SCREEN_TIME = ScreenTimeConfig.WIDGET_ID
    const val ID_SCREEN_TIME_LARGE = ScreenTimeLargeConfig.WIDGET_ID
    const val ID_QUICK_ACTIONS = QuickActionsConfig.WIDGET_ID

    /** Description and tags come from each widget's own `res/xml/<widget>_meta.xml`. */
    fun all(context: Context): List<WidgetItem> {
        val now = System.currentTimeMillis()
        return listOf(
            WidgetItem(
                id = ID_ROUND_CLOCK,
                name = "Round Clock",
                category = WidgetCategory.Clock,
                metadata = WidgetMetadata.read(context, R.xml.round_clock_meta),
                addedAtMillis = now,
                providerClassName = RoundClockWidgetReceiver::class.java.name,
                previewResId = R.drawable.round_clock_preview,
                previewCircular = true,
                customization = RoundClockCustomization,
            ),
            WidgetItem(
                id = ID_WEATHER,
                name = "Weather",
                category = WidgetCategory.Weather,
                metadata = WidgetMetadata.read(context, R.xml.weather_meta),
                addedAtMillis = now,
                providerClassName = WeatherWidgetReceiver::class.java.name,
                previewResId = R.drawable.weather_preview,
                previewCircular = false,
                customization = WeatherCustomization,
            ),
            WidgetItem(
                id = ID_SCREEN_TIME,
                name = "Screen Time",
                category = WidgetCategory.System,
                metadata = WidgetMetadata.read(context, R.xml.screen_time_meta),
                addedAtMillis = now,
                providerClassName = ScreenTimeWidgetReceiver::class.java.name,
                previewResId = R.drawable.screen_time_preview,
                previewCircular = false,
                customization = ScreenTimeCustomization,
            ),
            WidgetItem(
                id = ID_SCREEN_TIME_LARGE,
                name = "Screen Time Large",
                category = WidgetCategory.System,
                metadata = WidgetMetadata.read(context, R.xml.screen_time_large_meta),
                addedAtMillis = now,
                providerClassName = ScreenTimeLargeWidgetReceiver::class.java.name,
                previewResId = R.drawable.screen_time_large_preview,
                previewCircular = false,
                customization = ScreenTimeLargeCustomization,
            ),
            WidgetItem(
                id = ID_QUICK_ACTIONS,
                name = "Quick Actions",
                category = WidgetCategory.System,
                metadata = WidgetMetadata.read(context, R.xml.quick_actions_meta),
                addedAtMillis = now,
                providerClassName = QuickActionsWidgetReceiver::class.java.name,
                previewResId = R.drawable.quick_actions_preview,
                previewCircular = false,
                customization = QuickActionsCustomization,
            ),
        )
    }
}
