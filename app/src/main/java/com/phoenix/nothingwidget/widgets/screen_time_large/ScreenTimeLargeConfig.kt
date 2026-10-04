package com.phoenix.nothingwidget.widgets.screen_time_large

object ScreenTimeLargeConfig {
    const val WIDGET_ID = "screen_time_large"
    const val ACTION_REFRESH =
        "com.phoenix.nothingwidget.widgets.screen_time_large.ACTION_REFRESH"
    const val ACTION_SET_PAGE =
        "com.phoenix.nothingwidget.widgets.screen_time_large.ACTION_SET_PAGE"
    const val EXTRA_PAGE = "screen_time_large_page"

    const val PAGE_TODAY = 0
    const val PAGE_PRIVACY = 1
    const val PAGE_COUNT = 2

    /** Match small Screen Time refresh cadence. */
    const val REFRESH_INTERVAL_MS = 60_000L

    /** Max rows when the widget is tall. */
    const val TOP_APPS_COUNT = 12

    /** Min rows when resized short (~2 cells). */
    const val TOP_APPS_COUNT_COMPACT = 2
}
