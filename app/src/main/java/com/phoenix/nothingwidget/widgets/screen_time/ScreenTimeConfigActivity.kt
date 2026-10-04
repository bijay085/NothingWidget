package com.phoenix.nothingwidget.widgets.screen_time

import com.phoenix.nothingwidget.R
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomization
import com.phoenix.nothingwidget.ui.customization.WidgetConfigActivity

/** Launcher "Configure / Edit" entry → Screen Time customization. */
class ScreenTimeConfigActivity : WidgetConfigActivity() {
    override val widgetName: String by lazy { getString(R.string.widget_screen_time_name) }
    override val customization: WidgetCustomization = ScreenTimeCustomization
    override val previewCircular: Boolean = false
}
