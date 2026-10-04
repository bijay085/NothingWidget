package com.phoenix.nothingwidget.widgets.round_clock

import com.phoenix.nothingwidget.R
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomization
import com.phoenix.nothingwidget.ui.customization.WidgetConfigActivity

/** Launcher "Configure / Edit" entry → Round Clock customization. */
class RoundClockConfigActivity : WidgetConfigActivity() {
    override val widgetName: String by lazy { getString(R.string.widget_round_clock_name) }
    override val customization: WidgetCustomization = RoundClockCustomization
    override val previewCircular: Boolean = true
}
