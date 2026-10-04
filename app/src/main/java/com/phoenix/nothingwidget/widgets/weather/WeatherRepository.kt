package com.phoenix.nothingwidget.widgets.weather

import android.content.Context
import android.util.Log

/**
 * Weather data source for the WorkManager refresh.
 */
object WeatherRepository {

    private const val TAG = "WeatherRepository"

    suspend fun fetchWeather(context: Context): WeatherModel {
        if (!WeatherLocationHelper.hasLocationPermission(context)) {
            return WeatherCache.unavailable()
        }

        val saved = WeatherLocationStore.load(context)
        val coordinates = WeatherLocationHelper.resolveCoordinates(context)
            ?: saved?.let { WeatherCoordinates(it.latitude, it.longitude) }
            ?: return WeatherCache.load(context) ?: WeatherCache.unavailable()

        // Prefer a real geocoded city; refresh when missing or still the fallback.
        val placeName = saved?.name
            ?.takeIf { it.isNotBlank() && it != WeatherConfig.LOCATION_UNKNOWN }
            ?: WeatherLocationHelper.resolvePlaceName(context, coordinates)
                .trim()
                .ifBlank { WeatherConfig.LOCATION_UNKNOWN }
                .also { name -> WeatherLocationStore.save(context, name, coordinates) }

        return try {
            val current = OpenMeteoWeatherClient.fetchCurrent(
                latitude = coordinates.latitude,
                longitude = coordinates.longitude,
            )
            val weatherType = OpenMeteoWeatherClient.mapWeatherType(current.weatherCode)
            WeatherModel(
                temperatureCelsius = current.temperatureC,
                condition = OpenMeteoWeatherClient.conditionLabel(weatherType),
                location = placeName,
                weatherType = weatherType,
                iconResId = WeatherVisuals.icon(weatherType),
            ).also { WeatherCache.save(context, it) }
        } catch (error: Exception) {
            Log.e(TAG, "Weather fetch failed", error)
            (WeatherCache.load(context) ?: WeatherCache.unavailable()).copy(location = placeName)
        }
    }
}
