package com.phoenix.nothingwidget.widgets.round_clock

import android.content.Context
import android.widget.RemoteViews
import com.phoenix.nothingwidget.core.widget_config.ColorChoice
import com.phoenix.nothingwidget.core.widget_config.ElementProperty
import com.phoenix.nothingwidget.core.widget_config.ElementSpec
import com.phoenix.nothingwidget.core.widget_config.ElementStyleConfig
import com.phoenix.nothingwidget.core.widget_config.SizeChoice
import com.phoenix.nothingwidget.core.widget_config.StylePresets
import com.phoenix.nothingwidget.core.widget_config.ToggleChoice
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomization
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomizationConfig

object RoundClockCustomization : WidgetCustomization {

    const val ELEMENT_TIME = "time"
    const val ELEMENT_DATE = "date"
    const val ELEMENT_ALARM = "alarm"
    const val ELEMENT_GLOW = "glow"

    const val FONT_SANS = StylePresets.FONT_SANS
    const val FONT_NDOT = StylePresets.FONT_NDOT

    const val COLOR_BLACK = StylePresets.BLACK
    const val COLOR_DARK_NAVY = StylePresets.DARK_NAVY

    override val widgetId: String = RoundClockConfig.WIDGET_ID

    override val defaults = WidgetCustomizationConfig(
        widgetId = widgetId,
        backgroundColor = COLOR_DARK_NAVY,
        elements = mapOf(
            ELEMENT_TIME to ElementStyleConfig(
                elementId = ELEMENT_TIME,
                textColor = StylePresets.WHITE,
                fontFamily = FONT_NDOT,
                fontSize = 36,
            ),
            ELEMENT_DATE to ElementStyleConfig(
                elementId = ELEMENT_DATE,
                textColor = StylePresets.WHITE,
                fontFamily = FONT_SANS,
                fontSize = 14,
            ),
            ELEMENT_ALARM to ElementStyleConfig(
                elementId = ELEMENT_ALARM,
                textColor = StylePresets.ALARM_DEFAULT,
                fontFamily = FONT_SANS,
                fontSize = 15,
                fontWeight = ElementStyleConfig.FONT_WEIGHT_LIGHT,
            ),
            ELEMENT_GLOW to ElementStyleConfig(
                elementId = ELEMENT_GLOW,
                textColor = StylePresets.GLOW_RED,
                fontFamily = FONT_SANS,
                fontSize = 0,
                enabled = true,
            ),
        ),
    )

    override val backgroundChoices = listOf(
        ColorChoice("Black", COLOR_BLACK),
        ColorChoice("Dark Navy", COLOR_DARK_NAVY),
        ColorChoice("Transparent", 0x00000000),
    )

    override val elementSpecs = listOf(
        ElementSpec(
            elementId = ELEMENT_TIME,
            title = "Time",
            properties = setOf(ElementProperty.COLOR, ElementProperty.FONT, ElementProperty.SIZE),
            colorChoices = StylePresets.textColors,
            fontChoices = StylePresets.fontsDisplay,
            sizeChoices = listOf(SizeChoice("S", 30), SizeChoice("M", 36), SizeChoice("L", 42)),
        ),
        ElementSpec(
            elementId = ELEMENT_DATE,
            title = "Date",
            properties = setOf(ElementProperty.COLOR, ElementProperty.FONT, ElementProperty.SIZE),
            colorChoices = StylePresets.textColors,
            fontChoices = StylePresets.fontsAtmospheric,
            sizeChoices = listOf(SizeChoice("S", 12), SizeChoice("M", 14), SizeChoice("L", 16)),
        ),
        ElementSpec(
            elementId = ELEMENT_ALARM,
            title = "Alarm",
            properties = setOf(ElementProperty.COLOR, ElementProperty.FONT, ElementProperty.SIZE),
            colorChoices = StylePresets.textColors + ColorChoice("Soft", StylePresets.ALARM_DEFAULT),
            fontChoices = StylePresets.fontsAlarm,
            sizeChoices = listOf(SizeChoice("S", 12), SizeChoice("M", 15), SizeChoice("L", 18)),
        ),
        ElementSpec(
            elementId = ELEMENT_GLOW,
            title = "Glow",
            properties = setOf(ElementProperty.COLOR, ElementProperty.ENABLED),
            colorChoices = listOf(
                ColorChoice("Red", StylePresets.GLOW_RED),
                ColorChoice("Blue", StylePresets.BLUE),
                ColorChoice("Purple", StylePresets.PURPLE),
                ColorChoice("Green", StylePresets.GREEN),
                ColorChoice("Cyan", StylePresets.CYAN),
            ),
            toggleChoices = listOf(
                ToggleChoice("On", true),
                ToggleChoice("Off", false),
            ),
        ),
    )

    override fun previewViews(context: Context, config: WidgetCustomizationConfig): RemoteViews =
        RoundClockRenderer.build(context, config, interactive = false)

    override fun refresh(context: Context) = RoundClockRenderer.renderAll(context)
}
