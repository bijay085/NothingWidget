package com.phoenix.nothingwidget.widgets.weather

import android.content.Context
import android.widget.RemoteViews
import com.phoenix.nothingwidget.core.widget_config.ColorChoice
import com.phoenix.nothingwidget.core.widget_config.ElementProperty
import com.phoenix.nothingwidget.core.widget_config.ElementSpec
import com.phoenix.nothingwidget.core.widget_config.ElementStyleConfig
import com.phoenix.nothingwidget.core.widget_config.IconStyleChoice
import com.phoenix.nothingwidget.core.widget_config.SizeChoice
import com.phoenix.nothingwidget.core.widget_config.StylePresets
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomization
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomizationConfig

object WeatherCustomization : WidgetCustomization {

    const val ELEMENT_TEMPERATURE = "temperature"
    const val ELEMENT_UNIT = "unit"
    const val ELEMENT_CONDITION = "condition"
    const val ELEMENT_LOCATION = "location"
    const val ELEMENT_ICON = "icon"

    const val FONT_DEFAULT = StylePresets.FONT_DEFAULT

    const val COLOR_BLACK = StylePresets.BLACK
    const val COLOR_DARK_NAVY = StylePresets.DARK_NAVY
    const val COLOR_LIGHT = StylePresets.LIGHT

    override val widgetId: String = WeatherConfig.WIDGET_ID

    override val defaults = WidgetCustomizationConfig(
        widgetId = widgetId,
        backgroundColor = COLOR_DARK_NAVY,
        elements = mapOf(
            ELEMENT_TEMPERATURE to ElementStyleConfig(
                elementId = ELEMENT_TEMPERATURE,
                textColor = StylePresets.WHITE,
                fontFamily = FONT_DEFAULT,
                fontSize = 34,
            ),
            ELEMENT_UNIT to ElementStyleConfig(
                elementId = ELEMENT_UNIT,
                textColor = StylePresets.WEATHER_SECONDARY,
                fontFamily = FONT_DEFAULT,
                fontSize = 13,
            ),
            ELEMENT_CONDITION to ElementStyleConfig(
                elementId = ELEMENT_CONDITION,
                textColor = StylePresets.WEATHER_SECONDARY,
                fontFamily = FONT_DEFAULT,
                fontSize = 13,
            ),
            ELEMENT_LOCATION to ElementStyleConfig(
                elementId = ELEMENT_LOCATION,
                textColor = StylePresets.WEATHER_MUTED,
                fontFamily = FONT_DEFAULT,
                fontSize = 11,
            ),
            ELEMENT_ICON to ElementStyleConfig(
                elementId = ELEMENT_ICON,
                textColor = StylePresets.WHITE,
                fontFamily = FONT_DEFAULT,
                fontSize = 0,
                iconStyle = StylePresets.ICON_DEFAULT,
            ),
        ),
    )

    override val backgroundChoices = listOf(
        ColorChoice("Dark Navy", COLOR_DARK_NAVY),
        ColorChoice("Black", COLOR_BLACK),
        ColorChoice("Light", COLOR_LIGHT),
    )

    override val elementSpecs = listOf(
        ElementSpec(
            elementId = ELEMENT_TEMPERATURE,
            title = "Temperature",
            properties = setOf(ElementProperty.COLOR, ElementProperty.FONT, ElementProperty.SIZE),
            colorChoices = StylePresets.textColors,
            fontChoices = StylePresets.fontsDisplay,
            sizeChoices = listOf(SizeChoice("S", 28), SizeChoice("M", 34), SizeChoice("L", 40)),
        ),
        ElementSpec(
            elementId = ELEMENT_UNIT,
            title = "Unit",
            properties = setOf(ElementProperty.COLOR, ElementProperty.FONT),
            colorChoices = StylePresets.textColors + ColorChoice("Soft", StylePresets.WEATHER_SECONDARY),
            fontChoices = StylePresets.fontsFuturistic,
        ),
        ElementSpec(
            elementId = ELEMENT_CONDITION,
            title = "Condition",
            properties = setOf(ElementProperty.COLOR, ElementProperty.FONT),
            colorChoices = StylePresets.textColors + ColorChoice("Soft", StylePresets.WEATHER_SECONDARY),
            fontChoices = StylePresets.fontsCondition,
        ),
        ElementSpec(
            elementId = ELEMENT_LOCATION,
            title = "Location",
            properties = setOf(ElementProperty.COLOR, ElementProperty.FONT, ElementProperty.SIZE),
            colorChoices = StylePresets.textColors + ColorChoice("Gray", StylePresets.WEATHER_MUTED),
            fontChoices = StylePresets.fontsAtmospheric,
            sizeChoices = listOf(SizeChoice("S", 10), SizeChoice("M", 11), SizeChoice("L", 13)),
        ),
        ElementSpec(
            elementId = ELEMENT_ICON,
            title = "Weather Icon",
            properties = setOf(ElementProperty.ICON_STYLE, ElementProperty.COLOR),
            colorChoices = StylePresets.textColors + ColorChoice("Gray", StylePresets.GRAY),
            iconStyleChoices = listOf(
                IconStyleChoice("Default", StylePresets.ICON_DEFAULT),
                IconStyleChoice("Mono", StylePresets.ICON_MONO),
            ),
        ),
    )

    override fun previewViews(context: Context, config: WidgetCustomizationConfig): RemoteViews =
        WeatherWidgetRenderer.preview(context, config)

    override fun refresh(context: Context) = WeatherWidgetRenderer.renderAll(context)
}
