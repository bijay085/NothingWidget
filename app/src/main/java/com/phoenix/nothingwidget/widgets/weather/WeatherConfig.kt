package com.phoenix.nothingwidget.widgets.weather

/**
 * Configuration for the Weather widget.
 */
object WeatherConfig {
    const val WIDGET_ID: String = "weather"

    const val PREFS_NAME: String = "weather_widget_cache"

    const val KEY_TEMPERATURE_C: String = "temperature_celsius"
    const val KEY_CONDITION: String = "condition"
    const val KEY_WEATHER_TYPE: String = "weather_type"

    /** Periodic refresh interval (battery-friendly). */
    const val REFRESH_INTERVAL_MINUTES: Long = 45L

    /** Shown when geocoder / GPS cannot resolve a place name. */
    const val LOCATION_UNKNOWN: String = "Kathmandu"
    const val CONDITION_UNAVAILABLE: String = "Unavailable"
}
