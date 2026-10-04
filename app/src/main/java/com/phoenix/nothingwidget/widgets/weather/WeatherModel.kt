package com.phoenix.nothingwidget.widgets.weather

import androidx.annotation.DrawableRes

enum class WeatherType {
    Sunny,
    Cloudy,
    Rain,
    Storm,
    Snow,
    Fog,
}

/**
 * Weather payload for the home-screen widget.
 * Temperature is raw Celsius; display text is formatted from settings.
 */
data class WeatherModel(
    val temperatureCelsius: Double?,
    val condition: String,
    val location: String,
    val weatherType: WeatherType,
    @param:DrawableRes val iconResId: Int,
)
