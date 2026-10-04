package com.phoenix.nothingwidget.widgets.quick_actions

import android.content.Context
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomizationConfig
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomizationRepository

/**
 * Reads slot1–slot8 from the shared widget customization DataStore.
 * Returns only enabled slots (1–3 always; 4–8 when turned on).
 */
object QuickActionsSettings {

    data class Slot(
        val elementId: String,
        val action: QuickActionType,
        val enabled: Boolean,
    )

    fun load(context: Context): List<QuickActionType> {
        val config = WidgetCustomizationRepository.config(context, QuickActionsCustomization)
        return resolveVisible(config)
    }

    fun resolveSlots(config: WidgetCustomizationConfig): List<Slot> =
        QuickActionsConfig.SLOT_ELEMENTS.mapIndexed { index, elementId ->
            val element = config.element(elementId)
            val required = index < QuickActionsConfig.REQUIRED_SLOT_COUNT
            Slot(
                elementId = elementId,
                action = QuickActionType.fromKey(element.iconStyle),
                enabled = required || element.enabled,
            )
        }

    fun resolveVisible(config: WidgetCustomizationConfig): List<QuickActionType> =
        resolveSlots(config).filter { it.enabled }.map { it.action }
}
