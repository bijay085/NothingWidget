package com.phoenix.nothingwidget.widgets.round_clock

/**
 * Configuration for the Round Clock widget.
 */
object RoundClockConfig {
    const val WIDGET_ID: String = "round_clock"

    const val FORMAT_12_HOUR: String = "hh"
    const val FORMAT_24_HOUR: String = "hh"
    const val FORMAT_MINUTE: String = "mm"

    const val ACTION_REFRESH: String =
        "com.phoenix.nothingwidget.widgets.round_clock.ACTION_REFRESH"

    /** Remaining time at/under this → 100% glow. */
    const val GLOW_FULL_REMAINING_MS: Long = 10L * 60L * 1000L

    /** Remaining time at/over this → dim but still visible glow. */
    const val GLOW_DIM_REMAINING_MS: Long = 8L * 60L * 60L * 1000L

    /** Floor kept high so the red curve stays visible on OLED black. */
    const val GLOW_MIN_ALPHA: Int = 160
    const val GLOW_MAX_ALPHA: Int = 255

    const val ALARM_PLACEHOLDER: String = "--:--"
    const val ALARM_TIME_FORMAT: String = "hh:mm a"

    /** Widget self-refresh interval while an alarm is upcoming. */
    const val REFRESH_INTERVAL_MS: Long = 5L * 60L * 1000L
}
