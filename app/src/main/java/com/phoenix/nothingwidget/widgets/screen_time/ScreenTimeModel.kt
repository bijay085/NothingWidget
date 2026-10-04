package com.phoenix.nothingwidget.widgets.screen_time

/**
 * Today's screen-time snapshot for the widget.
 */
data class ScreenTimeModel(
    val totalScreenTimeToday: Long,
    val topAppName: String,
    val topAppUsageTime: Long,
    val hasPermission: Boolean,
    /** Top apps by usage (highest first). Used by Screen Time Large; small widget ignores this. */
    val topApps: List<TopAppUsageItem> = emptyList(),
) {
    val totalScreenTimeLabel: String
        get() = formatDuration(totalScreenTimeToday)

    val topAppUsageLabel: String
        get() = formatDuration(topAppUsageTime)

    companion object {
        fun unavailable(): ScreenTimeModel = ScreenTimeModel(
            totalScreenTimeToday = 0L,
            topAppName = "Grant access",
            topAppUsageTime = 0L,
            hasPermission = false,
            topApps = emptyList(),
        )

        fun empty(): ScreenTimeModel = ScreenTimeModel(
            totalScreenTimeToday = 0L,
            topAppName = "No usage yet",
            topAppUsageTime = 0L,
            hasPermission = true,
            topApps = emptyList(),
        )

        fun formatDuration(ms: Long): String {
            if (ms <= 0L) return "0m"
            val totalMinutes = ms / 60_000L
            val hours = totalMinutes / 60L
            val minutes = totalMinutes % 60L
            return when {
                hours > 0L && minutes > 0L -> "${hours}h ${minutes}m"
                hours > 0L -> "${hours}h"
                else -> "${minutes}m"
            }
        }
    }
}
