package com.phoenix.nothingwidget.widgets.weather

import com.phoenix.nothingwidget.R
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomization
import com.phoenix.nothingwidget.ui.customization.WidgetConfigActivity

/** Launcher "Configure / Edit" entry → Weather customization. */
class WeatherConfigActivity : WidgetConfigActivity() {
    override val widgetName: String by lazy { getString(R.string.widget_weather_name) }
    override val customization: WidgetCustomization = WeatherCustomization
    override val previewCircular: Boolean = false
}
