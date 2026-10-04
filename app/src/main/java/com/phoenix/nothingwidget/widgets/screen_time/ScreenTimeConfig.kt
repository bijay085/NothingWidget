package com.phoenix.nothingwidget.widgets.screen_time

object ScreenTimeConfig {
    const val WIDGET_ID = "screen_time"
    const val ACTION_REFRESH = "com.phoenix.nothingwidget.widgets.screen_time.ACTION_REFRESH"
    const val ACTION_OPEN_ACCESS = "com.phoenix.nothingwidget.widgets.screen_time.ACTION_OPEN_ACCESS"

    /** Self-refresh while the widget is pinned (AlarmManager + WorkManager chain). */
    const val REFRESH_INTERVAL_MS = 60_000L
}
