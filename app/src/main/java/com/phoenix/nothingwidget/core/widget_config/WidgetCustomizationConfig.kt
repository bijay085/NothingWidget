package com.phoenix.nothingwidget.core.widget_config

import android.content.Context
import android.widget.RemoteViews
import androidx.annotation.ColorInt

/**
 * Full style for one widget: shared background + per-element styles.
 * Defaults of each widget must reproduce the current XML look.
 */
data class WidgetCustomizationConfig(
    val widgetId: String,
    @param:ColorInt val backgroundColor: Int,
    val elements: Map<String, ElementStyleConfig>,
) {
    fun element(id: String): ElementStyleConfig =
        elements.getValue(id)

    fun withBackground(@ColorInt color: Int): WidgetCustomizationConfig =
        copy(backgroundColor = color)

    fun withElement(style: ElementStyleConfig): WidgetCustomizationConfig =
        copy(elements = elements + (style.elementId to style))
}

/**
 * What a widget contributes to the shared customization engine:
 * defaults, element option specs, and how to render / refresh.
 */
interface WidgetCustomization {
    val widgetId: String
    val defaults: WidgetCustomizationConfig
    val backgroundChoices: List<ColorChoice>
    val elementSpecs: List<ElementSpec>

    fun previewViews(context: Context, config: WidgetCustomizationConfig): RemoteViews

    fun refresh(context: Context)
}

/** Shared color / font presets used by widgets (no duplicates in each widget file). */
object StylePresets {
    const val FONT_SANS = "sans"
    const val FONT_NDOT = "ndot"
    const val FONT_DIGITAL = "digital"
    const val FONT_CLEAN = "clean"
    const val FONT_DEFAULT = "default"
    const val FONT_FUTURISTIC = "futuristic"
    const val FONT_PIXEL_MONO = "pixel_mono"
    const val FONT_DARK_SPOOKY = "dark_spooky"

    const val ICON_DEFAULT = "default"
    const val ICON_MONO = "mono"

    const val WHITE = 0xFFFFFFFF.toInt()
    const val GRAY = 0xFFA5B0C5.toInt()
    const val RED = 0xFFFF5A5A.toInt()
    const val BLUE = 0xFF6FA8FF.toInt()
    const val PURPLE = 0xFF9B6BFF.toInt()
    const val GREEN = 0xFF4ADE80.toInt()
    const val ORANGE = 0xFFFF9F43.toInt()
    const val YELLOW = 0xFFFFD166.toInt()
    const val CYAN = 0xFF2DD4BF.toInt()
    const val BLACK = 0xFF000000.toInt()
    const val DARK_NAVY = 0xFF243B5C.toInt()
    const val LIGHT = 0xFFEEF1F6.toInt()
    const val GLOW_RED = 0xFFFF3B3B.toInt()
    const val ALARM_DEFAULT = 0xFFF2F2F2.toInt()
    const val WEATHER_SECONDARY = 0xFFD0D6E5.toInt()
    const val WEATHER_MUTED = 0xFFA5B0C5.toInt()

    val textColors = listOf(
        ColorChoice("White", WHITE),
        ColorChoice("Red", RED),
        ColorChoice("Blue", BLUE),
        ColorChoice("Purple", PURPLE),
        ColorChoice("Green", GREEN),
        ColorChoice("Orange", ORANGE),
        ColorChoice("Yellow", YELLOW),
        ColorChoice("Cyan", CYAN),
    )

    /** Time / temperature. */
    val fontsDisplay = listOf(
        FontChoice("NDot", FONT_NDOT),
        FontChoice("Digital", FONT_DIGITAL),
        FontChoice("Clean", FONT_CLEAN),
        FontChoice("Futuristic", FONT_FUTURISTIC),
        FontChoice("Pixel Mono", FONT_PIXEL_MONO),
    )

    /** Unit. */
    val fontsFuturistic = listOf(
        FontChoice("Digital", FONT_DIGITAL),
        FontChoice("Clean", FONT_CLEAN),
        FontChoice("Futuristic", FONT_FUTURISTIC),
    )

    /** Date / location. */
    val fontsAtmospheric = listOf(
        FontChoice("NDot", FONT_NDOT),
        FontChoice("Digital", FONT_DIGITAL),
        FontChoice("Clean", FONT_CLEAN),
        FontChoice("Spooky", FONT_DARK_SPOOKY),
    )

    /** Alarm (no NDot variant in layout). */
    val fontsAlarm = listOf(
        FontChoice("Digital", FONT_DIGITAL),
        FontChoice("Clean", FONT_CLEAN),
        FontChoice("Spooky", FONT_DARK_SPOOKY),
    )

    /** Condition. */
    val fontsCondition = listOf(
        FontChoice("Digital", FONT_DIGITAL),
        FontChoice("Clean", FONT_CLEAN),
        FontChoice("Futuristic", FONT_FUTURISTIC),
        FontChoice("Spooky", FONT_DARK_SPOOKY),
    )
}
