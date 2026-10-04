package com.phoenix.nothingwidget.widgets.quick_actions

import com.phoenix.nothingwidget.R
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomization
import com.phoenix.nothingwidget.ui.customization.WidgetConfigActivity

/** Launcher / in-app Customize → Quick Actions modes. */
class QuickActionsConfigActivity : WidgetConfigActivity() {
    override val widgetName: String by lazy { getString(R.string.widget_quick_actions_name) }
    override val customization: WidgetCustomization = QuickActionsCustomization
    override val previewCircular: Boolean = false
}
