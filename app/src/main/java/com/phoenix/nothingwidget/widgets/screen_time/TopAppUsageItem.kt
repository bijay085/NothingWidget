package com.phoenix.nothingwidget.widgets.screen_time

/** One row in the Screen Time Large top-apps list. */
data class TopAppUsageItem(
    val appName: String,
    val usageTime: Long,
    val packageName: String,
) {
    val usageLabel: String
        get() = ScreenTimeModel.formatDuration(usageTime)
}
