package com.phoenix.nothingwidget.widgets.weather

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Free Open-Meteo current-conditions client (no API key).
 */
object OpenMeteoWeatherClient {

    private const val FORECAST_URL = "https://api.open-meteo.com/v1/forecast"
    private const val TIMEOUT_MS = 12_000

    data class CurrentWeather(
        val temperatureC: Double,
        val weatherCode: Int,
    )

    fun fetchCurrent(latitude: Double, longitude: Double): CurrentWeather {
        val url = URL(
            "$FORECAST_URL?latitude=$latitude&longitude=$longitude" +
                "&current=temperature_2m,weather_code&timezone=auto",
        )
        val connection = (url.openConnection() as HttpURLConnection).apply {
            connectTimeout = TIMEOUT_MS
            readTimeout = TIMEOUT_MS
            requestMethod = "GET"
        }
        try {
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val current = JSONObject(body).getJSONObject("current")
            return CurrentWeather(
                temperatureC = current.getDouble("temperature_2m"),
                weatherCode = current.getInt("weather_code"),
            )
        } finally {
            connection.disconnect()
        }
    }

    fun mapWeatherType(weatherCode: Int): WeatherType {
        return when (weatherCode) {
            0 -> WeatherType.Sunny
            in 1..3 -> WeatherType.Cloudy
            45, 48 -> WeatherType.Fog
            in 51..67, in 80..82 -> WeatherType.Rain
            in 71..77, 85, 86 -> WeatherType.Snow
            in 95..99 -> WeatherType.Storm
            else -> WeatherType.Cloudy
        }
    }

    fun conditionLabel(weatherType: WeatherType): String {
        return when (weatherType) {
            WeatherType.Sunny -> "Sunny"
            WeatherType.Cloudy -> "Cloudy"
            WeatherType.Rain -> "Rain"
            WeatherType.Storm -> "Storm"
            WeatherType.Snow -> "Snow"
            WeatherType.Fog -> "Fog"
        }
    }
}
