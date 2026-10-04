package com.phoenix.nothingwidget.core.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "nothing_widget_settings",
)

enum class TimeFormat(val storageValue: String, val label: String) {
    HOUR_12("12", "12 Hour"),
    HOUR_24("24", "24 Hour"),
    ;

    companion object {
        fun fromStorage(value: String?): TimeFormat {
            return entries.firstOrNull { it.storageValue == value } ?: HOUR_12
        }
    }
}

enum class TemperatureUnit(val storageValue: String, val label: String) {
    CELSIUS("C", "Celsius (°C)"),
    FAHRENHEIT("F", "Fahrenheit (°F)"),
    ;

    companion object {
        fun fromStorage(value: String?): TemperatureUnit {
            return entries.firstOrNull { it.storageValue == value } ?: CELSIUS
        }
    }
}

/**
 * Settings shared by the app shell and widgets.
 * Keys: time_format ("12"|"24"), temperature_unit ("C"|"F").
 */
object SettingsRepository {
    private val KEY_TIME_FORMAT = stringPreferencesKey("time_format")
    private val KEY_TEMPERATURE_UNIT = stringPreferencesKey("temperature_unit")

    fun timeFormatFlow(context: Context): Flow<TimeFormat> {
        return context.applicationContext.settingsDataStore.data.map { prefs ->
            TimeFormat.fromStorage(prefs[KEY_TIME_FORMAT])
        }
    }

    fun temperatureUnitFlow(context: Context): Flow<TemperatureUnit> {
        return context.applicationContext.settingsDataStore.data.map { prefs ->
            TemperatureUnit.fromStorage(prefs[KEY_TEMPERATURE_UNIT])
        }
    }

    /** Blocking read for synchronous RemoteViews paths. */
    fun temperatureUnit(context: Context): TemperatureUnit = runBlocking {
        temperatureUnitFlow(context).first()
    }

    suspend fun setTimeFormat(context: Context, format: TimeFormat) {
        context.applicationContext.settingsDataStore.edit { prefs ->
            prefs[KEY_TIME_FORMAT] = format.storageValue
        }
    }

    suspend fun setTemperatureUnit(context: Context, unit: TemperatureUnit) {
        context.applicationContext.settingsDataStore.edit { prefs ->
            prefs[KEY_TEMPERATURE_UNIT] = unit.storageValue
        }
    }
}
