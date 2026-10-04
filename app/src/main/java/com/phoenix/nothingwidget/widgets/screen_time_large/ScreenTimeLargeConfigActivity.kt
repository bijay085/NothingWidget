package com.phoenix.nothingwidget.widgets.screen_time_large

import com.phoenix.nothingwidget.R
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomization
import com.phoenix.nothingwidget.ui.customization.WidgetConfigActivity

/** Launcher "Configure / Edit" entry → Screen Time Large customization. */
class ScreenTimeLargeConfigActivity : WidgetConfigActivity() {
    override val widgetName: String by lazy { getString(R.string.widget_screen_time_large_name) }
    override val customization: WidgetCustomization = ScreenTimeLargeCustomization
    override val previewCircular: Boolean = false
}
