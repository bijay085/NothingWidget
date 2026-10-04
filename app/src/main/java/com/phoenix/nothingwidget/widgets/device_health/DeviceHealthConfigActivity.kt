package com.phoenix.nothingwidget.widgets.device_health

import com.phoenix.nothingwidget.R
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomization
import com.phoenix.nothingwidget.ui.customization.WidgetConfigActivity

class DeviceHealthConfigActivity : WidgetConfigActivity() {
    override val widgetName: String by lazy { getString(R.string.widget_device_health_name) }
    override val customization: WidgetCustomization = DeviceHealthCustomization
    override val previewCircular: Boolean = false
}
