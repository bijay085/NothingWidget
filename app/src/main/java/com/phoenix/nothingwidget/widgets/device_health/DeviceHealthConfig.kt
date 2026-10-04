package com.phoenix.nothingwidget.widgets.device_health

object DeviceHealthConfig {
    const val WIDGET_ID = "device_health"

    const val ACTION_REFRESH = "com.phoenix.nothingwidget.widgets.device_health.ACTION_REFRESH"
    const val ACTION_SET_PAGE = "com.phoenix.nothingwidget.widgets.device_health.ACTION_SET_PAGE"
    const val EXTRA_PAGE = "device_health_page"

    const val PAGE_OVERVIEW = 0
    const val PAGE_CHARGING = 1
    const val PAGE_DETAILS = 2
    const val PAGE_PERFORMANCE = 3
    const val PAGE_COUNT = 4

    /** @deprecated Use PAGE_OVERVIEW */
    const val PAGE_BATTERY = PAGE_OVERVIEW

    const val REFRESH_INTERVAL_MS = 60_000L

    const val ELEMENT_BATTERY_PERCENT = "battery_percent"
    const val ELEMENT_BATTERY_CHARGING = "battery_charging"
    const val ELEMENT_PERF_RAM = "perf_ram"
    const val ELEMENT_PERF_CPU = "perf_cpu"
    const val ELEMENT_PERF_TEMP = "perf_temp"
}
