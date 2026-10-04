package com.phoenix.nothingwidget.widgets.weather

import android.content.Context
import com.phoenix.nothingwidget.core.settings.SettingsRepository
import com.phoenix.nothingwidget.core.settings.TemperatureUnit
import kotlin.math.roundToInt

/**
 * Formats raw Celsius for the Weather widget using the shared temperature_unit setting.
 * Display is split: value ("18°") + unit ("C" / "F").
 */
object WeatherTemperature {

    private const val MISSING_VALUE = "--°"

    data class DisplayParts(
        val value: String,
        val unit: String,
    )

    fun formatParts(context: Context, celsius: Double?): DisplayParts {
        val unit = SettingsRepository.temperatureUnit(context)
        if (celsius == null) {
            return DisplayParts(value = MISSING_VALUE, unit = unit.storageValue)
        }
        val degrees = when (unit) {
            TemperatureUnit.CELSIUS -> celsius
            TemperatureUnit.FAHRENHEIT -> celsius * 9.0 / 5.0 + 32.0
        }.roundToInt()
        return DisplayParts(value = "$degrees°", unit = unit.storageValue)
    }
}
