package com.phoenix.nothingwidget.widgets.weather

import androidx.annotation.DrawableRes
import com.phoenix.nothingwidget.R

/**
 * Maps conditions to RemoteViews-safe icons.
 */
object WeatherVisuals {

    @DrawableRes
    fun icon(weatherType: WeatherType): Int = when (weatherType) {
        WeatherType.Sunny -> R.drawable.weather_ic_sun
        WeatherType.Cloudy -> R.drawable.weather_ic_cloud
        WeatherType.Rain -> R.drawable.weather_ic_rain
        WeatherType.Storm -> R.drawable.weather_ic_storm
        WeatherType.Snow -> R.drawable.weather_ic_snow
        WeatherType.Fog -> R.drawable.weather_ic_fog
    }
}
