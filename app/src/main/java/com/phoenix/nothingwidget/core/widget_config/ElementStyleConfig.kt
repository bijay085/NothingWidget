package com.phoenix.nothingwidget.core.widget_config

import androidx.annotation.ColorInt

/** Style of one named element inside a widget (time, location, glow, …). */
data class ElementStyleConfig(
    val elementId: String,
    @param:ColorInt val textColor: Int,
    val fontFamily: String,
    /** Size in sp. */
    val fontSize: Int,
    val fontWeight: String = FONT_WEIGHT_NORMAL,
    /** Used by glow / optional toggles. */
    val enabled: Boolean = true,
    /** Used by weather icon style. */
    val iconStyle: String? = null,
) {
    companion object {
        const val FONT_WEIGHT_NORMAL = "normal"
        const val FONT_WEIGHT_LIGHT = "light"
        const val FONT_WEIGHT_MEDIUM = "medium"
    }
}

enum class ElementProperty {
    COLOR,
    FONT,
    SIZE,
    ENABLED,
    ICON_STYLE,
}

data class SizeChoice(
    override val label: String,
    val sizeSp: Int,
) : StyleChoice

data class ToggleChoice(
    override val label: String,
    val enabled: Boolean,
) : StyleChoice

data class IconStyleChoice(
    override val label: String,
    val key: String,
) : StyleChoice

/** One customizable element offered by a widget in the shared UI. */
data class ElementSpec(
    val elementId: String,
    val title: String,
    val properties: Set<ElementProperty>,
    val colorChoices: List<ColorChoice> = emptyList(),
    val fontChoices: List<FontChoice> = emptyList(),
    val sizeChoices: List<SizeChoice> = emptyList(),
    val toggleChoices: List<ToggleChoice> = emptyList(),
    val iconStyleChoices: List<IconStyleChoice> = emptyList(),
)

fun ElementStyleConfig.isSelected(property: ElementProperty, choice: StyleChoice): Boolean =
    when (choice) {
        is ColorChoice -> property == ElementProperty.COLOR && textColor == choice.color
        is FontChoice -> property == ElementProperty.FONT && fontFamily == choice.key
        is SizeChoice -> property == ElementProperty.SIZE && fontSize == choice.sizeSp
        is ToggleChoice -> property == ElementProperty.ENABLED && enabled == choice.enabled
        is IconStyleChoice -> property == ElementProperty.ICON_STYLE && iconStyle == choice.key
    }

fun ElementStyleConfig.with(property: ElementProperty, choice: StyleChoice): ElementStyleConfig =
    when (choice) {
        is ColorChoice -> copy(textColor = choice.color)
        is FontChoice -> copy(fontFamily = choice.key)
        is SizeChoice -> copy(fontSize = choice.sizeSp)
        is ToggleChoice -> copy(enabled = choice.enabled)
        is IconStyleChoice -> copy(iconStyle = choice.key)
    }
