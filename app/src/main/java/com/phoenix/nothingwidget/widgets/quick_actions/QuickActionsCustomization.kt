package com.phoenix.nothingwidget.widgets.quick_actions

import android.content.Context
import android.widget.RemoteViews
import com.phoenix.nothingwidget.core.widget_config.ColorChoice
import com.phoenix.nothingwidget.core.widget_config.ElementProperty
import com.phoenix.nothingwidget.core.widget_config.ElementSpec
import com.phoenix.nothingwidget.core.widget_config.ElementStyleConfig
import com.phoenix.nothingwidget.core.widget_config.StylePresets
import com.phoenix.nothingwidget.core.widget_config.ToggleChoice
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomization
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomizationConfig

/**
 * Slots stored as `quick_actions.slotN.icon_style` (+ enabled for optional slots).
 * Choices come only from [QuickActionType].
 */
object QuickActionsCustomization : WidgetCustomization {

    const val COLOR_BACKGROUND = 0xFF101522.toInt()

    override val widgetId: String = QuickActionsConfig.WIDGET_ID

    private fun slotDefault(
        elementId: String,
        type: QuickActionType,
        enabled: Boolean,
    ) = ElementStyleConfig(
        elementId = elementId,
        textColor = StylePresets.WHITE,
        fontFamily = StylePresets.FONT_CLEAN,
        fontSize = 0,
        enabled = enabled,
        iconStyle = type.key,
    )

    override val defaults = WidgetCustomizationConfig(
        widgetId = widgetId,
        backgroundColor = COLOR_BACKGROUND,
        elements = mapOf(
            QuickActionsConfig.ELEMENT_SLOT1 to
                slotDefault(QuickActionsConfig.ELEMENT_SLOT1, QuickActionType.QR_SCANNER, true),
            QuickActionsConfig.ELEMENT_SLOT2 to
                slotDefault(QuickActionsConfig.ELEMENT_SLOT2, QuickActionType.FLASHLIGHT, true),
            QuickActionsConfig.ELEMENT_SLOT3 to
                slotDefault(QuickActionsConfig.ELEMENT_SLOT3, QuickActionType.CAMERA, true),
            QuickActionsConfig.ELEMENT_SLOT4 to
                slotDefault(QuickActionsConfig.ELEMENT_SLOT4, QuickActionType.MAPS, false),
            QuickActionsConfig.ELEMENT_SLOT5 to
                slotDefault(QuickActionsConfig.ELEMENT_SLOT5, QuickActionType.NOTES, false),
            QuickActionsConfig.ELEMENT_SLOT6 to
                slotDefault(QuickActionsConfig.ELEMENT_SLOT6, QuickActionType.CALENDAR, false),
            QuickActionsConfig.ELEMENT_SLOT7 to
                slotDefault(QuickActionsConfig.ELEMENT_SLOT7, QuickActionType.SCREENSHOT, false),
            QuickActionsConfig.ELEMENT_SLOT8 to
                slotDefault(QuickActionsConfig.ELEMENT_SLOT8, QuickActionType.SETTINGS, false),
        ),
    )

    override val backgroundChoices = listOf(
        ColorChoice("Dark", COLOR_BACKGROUND),
    )

    private val actionChoices = QuickActionType.choices()

    private val optionalToggle = listOf(
        ToggleChoice("Off", false),
        ToggleChoice("On", true),
    )

    private fun requiredSlotSpec(elementId: String, title: String) = ElementSpec(
        elementId = elementId,
        title = title,
        properties = setOf(ElementProperty.ICON_STYLE),
        iconStyleChoices = actionChoices,
    )

    private fun optionalSlotSpec(elementId: String, title: String) = ElementSpec(
        elementId = elementId,
        title = title,
        properties = setOf(ElementProperty.ENABLED, ElementProperty.ICON_STYLE),
        toggleChoices = optionalToggle,
        iconStyleChoices = actionChoices,
    )

    override val elementSpecs = listOf(
        requiredSlotSpec(QuickActionsConfig.ELEMENT_SLOT1, "Choose Action 1"),
        requiredSlotSpec(QuickActionsConfig.ELEMENT_SLOT2, "Choose Action 2"),
        requiredSlotSpec(QuickActionsConfig.ELEMENT_SLOT3, "Choose Action 3"),
        optionalSlotSpec(QuickActionsConfig.ELEMENT_SLOT4, "Choose Action 4"),
        optionalSlotSpec(QuickActionsConfig.ELEMENT_SLOT5, "Choose Action 5"),
        optionalSlotSpec(QuickActionsConfig.ELEMENT_SLOT6, "Choose Action 6"),
        optionalSlotSpec(QuickActionsConfig.ELEMENT_SLOT7, "Choose Action 7"),
        optionalSlotSpec(QuickActionsConfig.ELEMENT_SLOT8, "Choose Action 8"),
        // Info-only row (no properties) — rendered as secondary note in customize UI.
        ElementSpec(
            elementId = QuickActionsConfig.INFO_NOTE_ID,
            title = "More actions can be added. If the widget becomes wider than available space, " +
                "extra actions will be scrollable.",
            properties = emptySet(),
        ),
    )

    /**
     * Interface stub only — Customize uses [QuickActionsPreview] (Compose).
     * Do not host this RemoteViews in AppWidgetHostView (HorizontalScrollView fails).
     */
    override fun previewViews(context: Context, config: WidgetCustomizationConfig): RemoteViews =
        QuickActionsRenderer.preview(context, config)

    override fun refresh(context: Context) = QuickActionsRenderer.renderAll(context)
}
