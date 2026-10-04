package com.phoenix.nothingwidget.widgets.weather

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.weatherLocationDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "weather_location",
)

data class SavedWeatherLocation(
    val name: String,
    val latitude: Double,
    val longitude: Double,
)

/**
 * Single location store for the Weather widget (DataStore Preferences).
 * Key: weather_city (+ latitude / longitude). Excluded from backup.
 */
object WeatherLocationStore {

    private val KEY_CITY = stringPreferencesKey("weather_city")
    private val KEY_CITY_LEGACY = stringPreferencesKey("weather_location_name")
    private val KEY_LATITUDE = doublePreferencesKey("latitude")
    private val KEY_LONGITUDE = doublePreferencesKey("longitude")

    suspend fun save(
        context: Context,
        city: String,
        coordinates: WeatherCoordinates,
    ) {
        val safeCity = city.trim().ifBlank { WeatherConfig.LOCATION_UNKNOWN }
        context.applicationContext.weatherLocationDataStore.edit { prefs ->
            prefs[KEY_CITY] = safeCity
            prefs.remove(KEY_CITY_LEGACY)
            prefs[KEY_LATITUDE] = coordinates.latitude
            prefs[KEY_LONGITUDE] = coordinates.longitude
        }
    }

    suspend fun load(context: Context): SavedWeatherLocation? {
        val prefs = context.applicationContext.weatherLocationDataStore.data.first()
        val name = prefs.city() ?: return null
        val latitude = prefs[KEY_LATITUDE] ?: return null
        val longitude = prefs[KEY_LONGITUDE] ?: return null
        return SavedWeatherLocation(name = name, latitude = latitude, longitude = longitude)
    }

    suspend fun coordinates(context: Context): WeatherCoordinates? {
        val prefs = context.applicationContext.weatherLocationDataStore.data.first()
        val latitude = prefs[KEY_LATITUDE] ?: return null
        val longitude = prefs[KEY_LONGITUDE] ?: return null
        return WeatherCoordinates(latitude = latitude, longitude = longitude)
    }

    /** Blocking, never-blank city for synchronous RemoteViews paths. */
    fun displayCity(context: Context, fallback: String? = null): String {
        val saved = runBlocking {
            context.applicationContext.weatherLocationDataStore.data.first().city()
        }
        return saved
            ?: fallback?.trim()?.takeIf { it.isNotBlank() }
            ?: WeatherConfig.LOCATION_UNKNOWN
    }

    private fun Preferences.city(): String? =
        (this[KEY_CITY] ?: this[KEY_CITY_LEGACY])?.trim()?.takeIf { it.isNotBlank() }
}
