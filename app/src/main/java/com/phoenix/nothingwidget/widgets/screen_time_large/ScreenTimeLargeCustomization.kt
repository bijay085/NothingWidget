package com.phoenix.nothingwidget.widgets.screen_time_large

import android.content.Context
import android.widget.RemoteViews
import com.phoenix.nothingwidget.core.widget_config.ColorChoice
import com.phoenix.nothingwidget.core.widget_config.ElementProperty
import com.phoenix.nothingwidget.core.widget_config.ElementSpec
import com.phoenix.nothingwidget.core.widget_config.ElementStyleConfig
import com.phoenix.nothingwidget.core.widget_config.FontChoice
import com.phoenix.nothingwidget.core.widget_config.SizeChoice
import com.phoenix.nothingwidget.core.widget_config.StylePresets
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomization
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomizationConfig

object ScreenTimeLargeCustomization : WidgetCustomization {

    const val ELEMENT_TOTAL = "total"
    const val ELEMENT_LABEL = "label"
    const val ELEMENT_SECTION = "section"
    const val ELEMENT_APP = "app"
    const val ELEMENT_TIME = "time"
    const val ELEMENT_PRIVACY_TITLE = "privacy_title"
    const val ELEMENT_PRIVACY_APP = "privacy_app"
    const val ELEMENT_PRIVACY_TIME = "privacy_time"

    const val COLOR_BACKGROUND = 0xFF101522.toInt()
    const val COLOR_SECONDARY = 0xFFB7C0D6.toInt()
    const val COLOR_ACCENT = 0xFF7C6CFF.toInt()

    private val fonts = listOf(
        FontChoice("Clean", StylePresets.FONT_CLEAN),
        FontChoice("Digital", StylePresets.FONT_DIGITAL),
        FontChoice("Futuristic", StylePresets.FONT_FUTURISTIC),
        FontChoice("Pixel Mono", StylePresets.FONT_PIXEL_MONO),
        FontChoice("Spooky", StylePresets.FONT_DARK_SPOOKY),
    )

    override val widgetId: String = ScreenTimeLargeConfig.WIDGET_ID

    override val defaults = WidgetCustomizationConfig(
        widgetId = widgetId,
        backgroundColor = COLOR_BACKGROUND,
        elements = mapOf(
            ELEMENT_TOTAL to ElementStyleConfig(
                elementId = ELEMENT_TOTAL,
                textColor = StylePresets.WHITE,
                fontFamily = StylePresets.FONT_CLEAN,
                fontSize = 30,
            ),
            ELEMENT_LABEL to ElementStyleConfig(
                elementId = ELEMENT_LABEL,
                textColor = COLOR_SECONDARY,
                fontFamily = StylePresets.FONT_CLEAN,
                fontSize = 13,
            ),
            ELEMENT_SECTION to ElementStyleConfig(
                elementId = ELEMENT_SECTION,
                textColor = COLOR_ACCENT,
                fontFamily = StylePresets.FONT_CLEAN,
                fontSize = 12,
            ),
            ELEMENT_APP to ElementStyleConfig(
                elementId = ELEMENT_APP,
                textColor = StylePresets.WHITE,
                fontFamily = StylePresets.FONT_CLEAN,
                fontSize = 13,
            ),
            ELEMENT_TIME to ElementStyleConfig(
                elementId = ELEMENT_TIME,
                textColor = COLOR_SECONDARY,
                fontFamily = StylePresets.FONT_CLEAN,
                fontSize = 13,
            ),
            ELEMENT_PRIVACY_TITLE to ElementStyleConfig(
                elementId = ELEMENT_PRIVACY_TITLE,
                textColor = COLOR_SECONDARY,
                fontFamily = StylePresets.FONT_CLEAN,
                fontSize = 14,
            ),
            ELEMENT_PRIVACY_APP to ElementStyleConfig(
                elementId = ELEMENT_PRIVACY_APP,
                textColor = StylePresets.WHITE,
                fontFamily = StylePresets.FONT_CLEAN,
                fontSize = 15,
            ),
            ELEMENT_PRIVACY_TIME to ElementStyleConfig(
                elementId = ELEMENT_PRIVACY_TIME,
                textColor = COLOR_ACCENT,
                fontFamily = StylePresets.FONT_CLEAN,
                fontSize = 12,
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
            sizeChoices = listOf(SizeChoice("S", 26), SizeChoice("M", 30), SizeChoice("L", 36)),
        ),
        ElementSpec(
            elementId = ELEMENT_LABEL,
            title = "Today Label",
            properties = setOf(ElementProperty.COLOR, ElementProperty.FONT, ElementProperty.SIZE),
            colorChoices = StylePresets.textColors + ColorChoice("Soft", COLOR_SECONDARY),
            fontChoices = fonts,
            sizeChoices = listOf(SizeChoice("S", 11), SizeChoice("M", 13), SizeChoice("L", 15)),
        ),
        ElementSpec(
            elementId = ELEMENT_SECTION,
            title = "Top Apps Label",
            properties = setOf(ElementProperty.COLOR, ElementProperty.FONT, ElementProperty.SIZE),
            colorChoices = StylePresets.textColors + ColorChoice("Accent", COLOR_ACCENT),
            fontChoices = fonts,
            sizeChoices = listOf(SizeChoice("S", 11), SizeChoice("M", 12), SizeChoice("L", 14)),
        ),
        ElementSpec(
            elementId = ELEMENT_APP,
            title = "App Names",
            properties = setOf(ElementProperty.COLOR, ElementProperty.FONT, ElementProperty.SIZE),
            colorChoices = StylePresets.textColors,
            fontChoices = fonts,
            sizeChoices = listOf(SizeChoice("S", 12), SizeChoice("M", 13), SizeChoice("L", 15)),
        ),
        ElementSpec(
            elementId = ELEMENT_TIME,
            title = "App Times",
            properties = setOf(ElementProperty.COLOR, ElementProperty.FONT, ElementProperty.SIZE),
            colorChoices = StylePresets.textColors + ColorChoice("Soft", COLOR_SECONDARY),
            fontChoices = fonts,
            sizeChoices = listOf(SizeChoice("S", 12), SizeChoice("M", 13), SizeChoice("L", 15)),
        ),
        ElementSpec(
            elementId = ELEMENT_PRIVACY_TITLE,
            title = "Privacy Title",
            properties = setOf(ElementProperty.COLOR, ElementProperty.SIZE),
            colorChoices = StylePresets.textColors + ColorChoice("Soft", COLOR_SECONDARY),
            sizeChoices = listOf(SizeChoice("S", 12), SizeChoice("M", 14), SizeChoice("L", 16)),
        ),
        ElementSpec(
            elementId = ELEMENT_PRIVACY_APP,
            title = "Privacy App Name",
            properties = setOf(ElementProperty.COLOR, ElementProperty.SIZE),
            colorChoices = StylePresets.textColors,
            sizeChoices = listOf(SizeChoice("S", 13), SizeChoice("M", 15), SizeChoice("L", 17)),
        ),
        ElementSpec(
            elementId = ELEMENT_PRIVACY_TIME,
            title = "Privacy Time",
            properties = setOf(ElementProperty.COLOR, ElementProperty.SIZE),
            colorChoices = StylePresets.textColors + ColorChoice("Accent", COLOR_ACCENT),
            sizeChoices = listOf(SizeChoice("S", 11), SizeChoice("M", 12), SizeChoice("L", 14)),
        ),
    )

    override fun previewViews(context: Context, config: WidgetCustomizationConfig): RemoteViews =
        ScreenTimeLargeRenderer.preview(context, config)

    override fun refresh(context: Context) = ScreenTimeLargeRenderer.renderAll(context)
}
