package com.phoenix.nothingwidget.widgets.weather

import android.content.Context

/**
 * Last fetched weather, kept in SharedPreferences for fast synchronous RemoteViews reads.
 * City is owned by [WeatherLocationStore], not stored here.
 */
object WeatherCache {

    fun save(context: Context, model: WeatherModel) {
        prefs(context).edit()
            .clear()
            .putString(WeatherConfig.KEY_TEMPERATURE_C, model.temperatureCelsius?.toString())
            .putString(WeatherConfig.KEY_CONDITION, model.condition)
            .putString(WeatherConfig.KEY_WEATHER_TYPE, model.weatherType.name)
            .apply()
    }

    fun load(context: Context): WeatherModel? {
        val prefs = prefs(context)
        val weatherType = prefs.getString(WeatherConfig.KEY_WEATHER_TYPE, null)
            ?.let { name -> runCatching { WeatherType.valueOf(name) }.getOrNull() }
            ?: return null
        return WeatherModel(
            temperatureCelsius = prefs.getString(WeatherConfig.KEY_TEMPERATURE_C, null)
                ?.toDoubleOrNull(),
            condition = prefs.getString(WeatherConfig.KEY_CONDITION, null)
                ?: OpenMeteoWeatherClient.conditionLabel(weatherType),
            location = WeatherConfig.LOCATION_UNKNOWN,
            weatherType = weatherType,
            iconResId = WeatherVisuals.icon(weatherType),
        )
    }

    fun unavailable(): WeatherModel {
        return WeatherModel(
            temperatureCelsius = null,
            condition = WeatherConfig.CONDITION_UNAVAILABLE,
            location = WeatherConfig.LOCATION_UNKNOWN,
            weatherType = WeatherType.Cloudy,
            iconResId = WeatherVisuals.icon(WeatherType.Cloudy),
        )
    }

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(WeatherConfig.PREFS_NAME, Context.MODE_PRIVATE)
}
