package com.phoenix.nothingwidget.widgets.screen_time_large

object ScreenTimeLargeConfig {
    const val WIDGET_ID = "screen_time_large"
    const val ACTION_REFRESH =
        "com.phoenix.nothingwidget.widgets.screen_time_large.ACTION_REFRESH"

    /** Match small Screen Time refresh cadence. */
    const val REFRESH_INTERVAL_MS = 60_000L

    /** Max rows when the widget is tall. */
    const val TOP_APPS_COUNT = 12

    /** Min rows when resized short (~2 cells). */
    const val TOP_APPS_COUNT_COMPACT = 2
}
