package com.phoenix.nothingwidget.widgets.screen_time

import android.content.Context
import android.widget.RemoteViews
import com.phoenix.nothingwidget.core.widget_config.ColorChoice
import com.phoenix.nothingwidget.core.widget_config.ElementProperty
import com.phoenix.nothingwidget.core.widget_config.ElementSpec
import com.phoenix.nothingwidget.core.widget_config.ElementStyleConfig
import com.phoenix.nothingwidget.core.widget_config.FontChoice
import com.phoenix.nothingwidget.core.widget_config.IconStyleChoice
import com.phoenix.nothingwidget.core.widget_config.SizeChoice
import com.phoenix.nothingwidget.core.widget_config.StylePresets
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomization
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomizationConfig

object ScreenTimeCustomization : WidgetCustomization {

    const val ELEMENT_TOTAL = "total"
    const val ELEMENT_LABEL = "label"
    const val ELEMENT_APP = "app"
    const val ELEMENT_INDICATOR = "indicator"

    const val COLOR_BACKGROUND = 0xFF101522.toInt()
    const val COLOR_SECONDARY = 0xFFB7C0D6.toInt()
    const val COLOR_ACCENT = 0xFF7C6CFF.toInt()

    const val INDICATOR_RING = "ring"
    const val INDICATOR_BAR = "bar"
    const val INDICATOR_NONE = "none"

    private val fonts = listOf(
        FontChoice("Clean", StylePresets.FONT_CLEAN),
        FontChoice("Digital", StylePresets.FONT_DIGITAL),
        FontChoice("Futuristic", StylePresets.FONT_FUTURISTIC),
        FontChoice("Pixel Mono", StylePresets.FONT_PIXEL_MONO),
        FontChoice("Spooky", StylePresets.FONT_DARK_SPOOKY),
    )

    override val widgetId: String = ScreenTimeConfig.WIDGET_ID

    override val defaults = WidgetCustomizationConfig(
        widgetId = widgetId,
        backgroundColor = COLOR_BACKGROUND,
        elements = mapOf(
            ELEMENT_TOTAL to ElementStyleConfig(
                elementId = ELEMENT_TOTAL,
                textColor = StylePresets.WHITE,
                fontFamily = StylePresets.FONT_CLEAN,
                fontSize = 28,
            ),
            ELEMENT_LABEL to ElementStyleConfig(
                elementId = ELEMENT_LABEL,
                textColor = COLOR_SECONDARY,
                fontFamily = StylePresets.FONT_CLEAN,
                fontSize = 12,
            ),
            ELEMENT_APP to ElementStyleConfig(
                elementId = ELEMENT_APP,
                textColor = COLOR_ACCENT,
                fontFamily = StylePresets.FONT_CLEAN,
                fontSize = 13,
            ),
            ELEMENT_INDICATOR to ElementStyleConfig(
                elementId = ELEMENT_INDICATOR,
                textColor = COLOR_ACCENT,
                fontFamily = StylePresets.FONT_CLEAN,
                fontSize = 0,
                iconStyle = INDICATOR_NONE,
            ),
        ),
    )

    override val backgroundChoices = listOf(
        ColorChoice("Dark", COLOR_BACKGROUND),
    )

    override val elementSpecs = listOf(
        ElementSpec(
            elementId = ELEMENT_TOTAL,
            title = "Total Screen Time",
            properties = setOf(ElementProperty.COLOR, ElementProperty.FONT, ElementProperty.SIZE),
            colorChoices = StylePresets.textColors,
            fontChoices = fonts,
            sizeChoices = listOf(SizeChoice("S", 24), SizeChoice("M", 28), SizeChoice("L", 32)),
        ),
        ElementSpec(
            elementId = ELEMENT_LABEL,
            title = "Label Text",
            properties = setOf(ElementProperty.COLOR, ElementProperty.FONT, ElementProperty.SIZE),
            colorChoices = StylePresets.textColors + ColorChoice("Soft", COLOR_SECONDARY),
            fontChoices = fonts,
            sizeChoices = listOf(SizeChoice("S", 11), SizeChoice("M", 12), SizeChoice("L", 14)),
        ),
        ElementSpec(
            elementId = ELEMENT_APP,
            title = "Top App",
            properties = setOf(ElementProperty.COLOR, ElementProperty.FONT, ElementProperty.SIZE),
            colorChoices = StylePresets.textColors + ColorChoice("Accent", COLOR_ACCENT),
            fontChoices = fonts,
            sizeChoices = listOf(SizeChoice("S", 12), SizeChoice("M", 13), SizeChoice("L", 15)),
        ),
        ElementSpec(
            elementId = ELEMENT_INDICATOR,
            title = "Usage Indicator",
            properties = setOf(ElementProperty.COLOR, ElementProperty.ICON_STYLE),
            colorChoices = StylePresets.textColors + ColorChoice("Accent", COLOR_ACCENT),
            iconStyleChoices = listOf(
                IconStyleChoice("None", INDICATOR_NONE),
                IconStyleChoice("Ring", INDICATOR_RING),
                IconStyleChoice("Bar", INDICATOR_BAR),
            ),
        ),
    )

    override fun previewViews(context: Context, config: WidgetCustomizationConfig): RemoteViews =
        ScreenTimeWidgetRenderer.preview(context, config)

    override fun refresh(context: Context) = ScreenTimeWidgetRenderer.renderAll(context)
}
