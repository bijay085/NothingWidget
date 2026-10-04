package com.phoenix.nothingwidget.core.widget_config

import android.content.Context
import android.graphics.Color
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.widgetStyleDataStore by preferencesDataStore(name = "widget_customization")

/**
 * One DataStore for every widget.
 * Keys: `widgetId.background`, `widgetId.elementId.color|font|size|weight|enabled|icon_style`
 * Example: `round_clock.time.color`, `weather.location.font`
 */
object WidgetCustomizationStore {

    private const val BACKGROUND = "background"
    private const val COLOR = "color"
    private const val FONT = "font"
    private const val SIZE = "size"
    private const val WEIGHT = "weight"
    private const val ENABLED = "enabled"
    private const val ICON_STYLE = "icon_style"

    fun flow(context: Context, defaults: WidgetCustomizationConfig): Flow<WidgetCustomizationConfig> =
        context.applicationContext.widgetStyleDataStore.data.map { prefs -> prefs.read(defaults) }

    suspend fun save(context: Context, config: WidgetCustomizationConfig) {
        context.applicationContext.widgetStyleDataStore.edit { prefs ->
            prefs[key(config.widgetId, BACKGROUND)] = config.backgroundColor.toHex()
            config.elements.values.forEach { element ->
                val id = config.widgetId
                val el = element.elementId
                prefs[key(id, el, COLOR)] = element.textColor.toHex()
                prefs[key(id, el, FONT)] = element.fontFamily
                prefs[intKey(id, el, SIZE)] = element.fontSize
                prefs[key(id, el, WEIGHT)] = element.fontWeight
                prefs[boolKey(id, el, ENABLED)] = element.enabled
                val icon = element.iconStyle
                if (icon != null) {
                    prefs[key(id, el, ICON_STYLE)] = icon
                } else {
                    prefs.remove(key(id, el, ICON_STYLE))
                }
            }
        }
    }

    suspend fun clear(context: Context, defaults: WidgetCustomizationConfig) {
        context.applicationContext.widgetStyleDataStore.edit { prefs ->
            prefs.remove(key(defaults.widgetId, BACKGROUND))
            defaults.elements.keys.forEach { el ->
                prefs.remove(key(defaults.widgetId, el, COLOR))
                prefs.remove(key(defaults.widgetId, el, FONT))
                prefs.remove(intKey(defaults.widgetId, el, SIZE))
                prefs.remove(key(defaults.widgetId, el, WEIGHT))
                prefs.remove(boolKey(defaults.widgetId, el, ENABLED))
                prefs.remove(key(defaults.widgetId, el, ICON_STYLE))
            }
            // Drop legacy flat keys from the previous widget-level store.
            prefs.removeLegacy(defaults.widgetId)
        }
    }

    private fun Preferences.read(defaults: WidgetCustomizationConfig): WidgetCustomizationConfig {
        val id = defaults.widgetId
        val background = colorOrNull(key(id, BACKGROUND))
            ?: legacyColor(id, "background", defaults.backgroundColor)
        val elements = defaults.elements.mapValues { (elementId, fallback) ->
            readElement(id, elementId, fallback)
        }
        return WidgetCustomizationConfig(
            widgetId = id,
            backgroundColor = background,
            elements = elements,
        )
    }

    private fun Preferences.readElement(
        widgetId: String,
        elementId: String,
        fallback: ElementStyleConfig,
    ): ElementStyleConfig {
        val color = colorOrNull(key(widgetId, elementId, COLOR))
            ?: legacyElementColor(widgetId, elementId, fallback.textColor)
        return ElementStyleConfig(
            elementId = elementId,
            textColor = color,
            fontFamily = this[key(widgetId, elementId, FONT)]
                ?: legacyElementFont(widgetId, elementId, fallback.fontFamily),
            fontSize = this[intKey(widgetId, elementId, SIZE)] ?: fallback.fontSize,
            fontWeight = this[key(widgetId, elementId, WEIGHT)] ?: fallback.fontWeight,
            enabled = this[boolKey(widgetId, elementId, ENABLED)] ?: fallback.enabled,
            iconStyle = this[key(widgetId, elementId, ICON_STYLE)] ?: fallback.iconStyle,
        )
    }

    /** Map old flat primary/secondary/accent/font into the closest element. */
    private fun Preferences.legacyElementColor(
        widgetId: String,
        elementId: String,
        fallback: Int,
    ): Int {
        val legacyField = when (elementId) {
            "time", "temperature" -> "primary_text_color"
            "location", "date" -> "secondary_text_color"
            "glow" -> "accent_color"
            else -> return fallback
        }
        return legacyColor(widgetId, legacyField, fallback)
    }

    private fun Preferences.legacyElementFont(
        widgetId: String,
        elementId: String,
        fallback: String,
    ): String {
        if (elementId != "time" && elementId != "temperature") return fallback
        val stored = this[stringPreferencesKey("${widgetId}_font_family")] ?: return fallback
        // Old Round Clock used key "default" for the sans option.
        return if (elementId == "time" && stored == "default") StylePresets.FONT_SANS else stored
    }

    private fun Preferences.legacyColor(widgetId: String, field: String, fallback: Int): Int {
        val stored = this[stringPreferencesKey("${widgetId}_$field")] ?: return fallback
        return runCatching { Color.parseColor(stored) }.getOrDefault(fallback)
    }

    private fun Preferences.colorOrNull(key: Preferences.Key<String>): Int? {
        val stored = this[key] ?: return null
        return runCatching { Color.parseColor(stored) }.getOrNull()
    }

    private fun MutablePreferences.removeLegacy(widgetId: String) {
        listOf(
            "background",
            "primary_text_color",
            "secondary_text_color",
            "accent_color",
            "font_family",
            "corner_style",
        ).forEach { field -> remove(stringPreferencesKey("${widgetId}_$field")) }
        remove(androidx.datastore.preferences.core.floatPreferencesKey("${widgetId}_font_size_scale"))
    }

    private fun key(widgetId: String, field: String) =
        stringPreferencesKey("$widgetId.$field")

    private fun key(widgetId: String, elementId: String, field: String) =
        stringPreferencesKey("$widgetId.$elementId.$field")

    private fun intKey(widgetId: String, elementId: String, field: String) =
        intPreferencesKey("$widgetId.$elementId.$field")

    private fun boolKey(widgetId: String, elementId: String, field: String) =
        booleanPreferencesKey("$widgetId.$elementId.$field")

    private fun Int.toHex(): String =
        if (Color.alpha(this) == 0xFF) "#%06X".format(this and 0xFFFFFF) else "#%08X".format(this)
}
