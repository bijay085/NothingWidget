package com.phoenix.nothingwidget.ui.data

import com.phoenix.nothingwidget.R
import com.phoenix.nothingwidget.ui.model.WidgetCategory
import com.phoenix.nothingwidget.ui.model.WidgetItem
import com.phoenix.nothingwidget.widgets.round_clock.RoundClockConfig
import com.phoenix.nothingwidget.widgets.round_clock.RoundClockCustomization
import com.phoenix.nothingwidget.widgets.round_clock.RoundClockWidgetReceiver
import com.phoenix.nothingwidget.widgets.weather.WeatherConfig
import com.phoenix.nothingwidget.widgets.weather.WeatherCustomization
import com.phoenix.nothingwidget.widgets.weather.WeatherWidgetReceiver

object WidgetCatalog {
    const val ID_ROUND_CLOCK = RoundClockConfig.WIDGET_ID
    const val ID_WEATHER = WeatherConfig.WIDGET_ID

    fun all(): List<WidgetItem> {
        val now = System.currentTimeMillis()
        return listOf(
            WidgetItem(
                id = ID_ROUND_CLOCK,
                name = "Round Clock",
                category = WidgetCategory.Clock,
                tags = listOf("2×2", "Non-resizable"),
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
                tags = listOf("2×2"),
                addedAtMillis = now,
                providerClassName = WeatherWidgetReceiver::class.java.name,
                previewResId = R.drawable.weather_preview,
                previewCircular = false,
                customization = WeatherCustomization,
            ),
        )
    }
}
