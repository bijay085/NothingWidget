package com.phoenix.nothingwidget.widgets.device_health

import android.content.Context
import android.widget.RemoteViews
import com.phoenix.nothingwidget.core.widget_config.ColorChoice
import com.phoenix.nothingwidget.core.widget_config.ElementProperty
import com.phoenix.nothingwidget.core.widget_config.ElementSpec
import com.phoenix.nothingwidget.core.widget_config.ElementStyleConfig
import com.phoenix.nothingwidget.core.widget_config.FontChoice
import com.phoenix.nothingwidget.core.widget_config.StylePresets
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomization
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomizationConfig

object DeviceHealthCustomization : WidgetCustomization {

    const val COLOR_BACKGROUND = 0xFF101522.toInt()
    const val COLOR_SECONDARY = 0xFFB7C0D6.toInt()
    const val COLOR_ACCENT = 0xFF7C6CFF.toInt()

    private val fonts = listOf(
        FontChoice("Clean", StylePresets.FONT_CLEAN),
        FontChoice("Digital", StylePresets.FONT_DIGITAL),
        FontChoice("Futuristic", StylePresets.FONT_FUTURISTIC),
        FontChoice("Pixel Mono", StylePresets.FONT_PIXEL_MONO),
    )

    override val widgetId: String = DeviceHealthConfig.WIDGET_ID

    private fun style(
        id: String,
        color: Int,
        font: String = StylePresets.FONT_CLEAN,
        size: Int = 0,
    ) = ElementStyleConfig(
        elementId = id,
        textColor = color,
        fontFamily = font,
        fontSize = size,
    )

    override val defaults = WidgetCustomizationConfig(
        widgetId = widgetId,
        backgroundColor = COLOR_BACKGROUND,
        elements = mapOf(
            DeviceHealthConfig.ELEMENT_BATTERY_PERCENT to
                style(DeviceHealthConfig.ELEMENT_BATTERY_PERCENT, StylePresets.WHITE, size = 40),
            DeviceHealthConfig.ELEMENT_BATTERY_CHARGING to
                style(DeviceHealthConfig.ELEMENT_BATTERY_CHARGING, COLOR_ACCENT, size = 14),
            DeviceHealthConfig.ELEMENT_PERF_RAM to
                style(DeviceHealthConfig.ELEMENT_PERF_RAM, StylePresets.WHITE, size = 15),
            DeviceHealthConfig.ELEMENT_PERF_CPU to
                style(DeviceHealthConfig.ELEMENT_PERF_CPU, StylePresets.WHITE, size = 15),
            DeviceHealthConfig.ELEMENT_PERF_TEMP to
                style(DeviceHealthConfig.ELEMENT_PERF_TEMP, StylePresets.WHITE, size = 15),
        ),
    )

    override val backgroundChoices = listOf(
        ColorChoice("Dark", COLOR_BACKGROUND),
    )

    override val elementSpecs = listOf(
        ElementSpec(
            elementId = DeviceHealthConfig.ELEMENT_BATTERY_PERCENT,
            title = "Battery · Percentage",
            properties = setOf(ElementProperty.COLOR, ElementProperty.FONT),
            colorChoices = StylePresets.textColors,
            fontChoices = fonts,
        ),
        ElementSpec(
            elementId = DeviceHealthConfig.ELEMENT_BATTERY_CHARGING,
            title = "Battery · Charging",
            properties = setOf(ElementProperty.COLOR, ElementProperty.FONT),
            colorChoices = StylePresets.textColors + ColorChoice("Accent", COLOR_ACCENT),
            fontChoices = fonts,
        ),
        ElementSpec(
            elementId = DeviceHealthConfig.ELEMENT_PERF_RAM,
            title = "Performance · RAM",
            properties = setOf(ElementProperty.COLOR, ElementProperty.FONT),
            colorChoices = StylePresets.textColors,
            fontChoices = fonts,
        ),
        ElementSpec(
            elementId = DeviceHealthConfig.ELEMENT_PERF_CPU,
            title = "Performance · CPU",
            properties = setOf(ElementProperty.COLOR, ElementProperty.FONT),
            colorChoices = StylePresets.textColors,
            fontChoices = fonts,
        ),
        ElementSpec(
            elementId = DeviceHealthConfig.ELEMENT_PERF_TEMP,
            title = "Performance · Temperature",
            properties = setOf(ElementProperty.COLOR, ElementProperty.FONT),
            colorChoices = StylePresets.textColors + ColorChoice("Soft", COLOR_SECONDARY),
            fontChoices = fonts,
        ),
    )

    override fun previewViews(context: Context, config: WidgetCustomizationConfig): RemoteViews =
        DeviceHealthRenderer.preview(context, config)

    override fun refresh(context: Context) = DeviceHealthRenderer.renderAll(context)
}
