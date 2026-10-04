package com.phoenix.nothingwidget.widgets.quick_actions

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.phoenix.nothingwidget.R
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomizationConfig
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomizationRepository
import com.phoenix.nothingwidget.core.widget_config.setSolidBackground

object QuickActionsRenderer {

    private data class SlotUi(
        val rootId: Int,
        val iconId: Int,
        val labelId: Int,
        val requestCode: Int,
    )

    private val SLOT_UI = listOf(
        SlotUi(R.id.quick_actions_slot1, R.id.quick_actions_slot1_icon, R.id.quick_actions_slot1_label, 6101),
        SlotUi(R.id.quick_actions_slot2, R.id.quick_actions_slot2_icon, R.id.quick_actions_slot2_label, 6102),
        SlotUi(R.id.quick_actions_slot3, R.id.quick_actions_slot3_icon, R.id.quick_actions_slot3_label, 6103),
        SlotUi(R.id.quick_actions_slot4, R.id.quick_actions_slot4_icon, R.id.quick_actions_slot4_label, 6104),
        SlotUi(R.id.quick_actions_slot5, R.id.quick_actions_slot5_icon, R.id.quick_actions_slot5_label, 6105),
        SlotUi(R.id.quick_actions_slot6, R.id.quick_actions_slot6_icon, R.id.quick_actions_slot6_label, 6106),
        SlotUi(R.id.quick_actions_slot7, R.id.quick_actions_slot7_icon, R.id.quick_actions_slot7_label, 6107),
        SlotUi(R.id.quick_actions_slot8, R.id.quick_actions_slot8_icon, R.id.quick_actions_slot8_label, 6108),
    )

    fun renderAll(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(
            ComponentName(context, QuickActionsWidgetReceiver::class.java),
        )
        if (ids.isNotEmpty()) {
            render(context, manager, ids)
        }
    }

    fun render(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        val config = WidgetCustomizationRepository.config(context, QuickActionsCustomization)
        val slots = QuickActionsSettings.resolveSlots(config)
        val views = build(context, slots, config.backgroundColor, interactive = true)
        appWidgetIds.forEach { id ->
            appWidgetManager.updateAppWidget(id, views)
        }
    }

    fun preview(context: Context, config: WidgetCustomizationConfig): RemoteViews {
        val slots = QuickActionsSettings.resolveSlots(config)
        return build(context, slots, config.backgroundColor, interactive = false)
    }

    private fun build(
        context: Context,
        slots: List<QuickActionsSettings.Slot>,
        backgroundColor: Int,
        interactive: Boolean,
    ): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_quick_actions)

        views.setSolidBackground(
            viewId = R.id.widget_quick_actions_root,
            color = backgroundColor,
            tintableBaseRes = R.drawable.quick_actions_background_tintable,
            presetResFor = { color ->
                if (color == QuickActionsCustomization.COLOR_BACKGROUND) {
                    R.drawable.quick_actions_background
                } else {
                    null
                }
            },
        )

        SLOT_UI.forEachIndexed { index, slotUi ->
            val slot = slots.getOrNull(index)
            if (slot == null || !slot.enabled) {
                views.setViewVisibility(slotUi.rootId, View.GONE)
                return@forEachIndexed
            }

            views.setViewVisibility(slotUi.rootId, View.VISIBLE)
            val action = slot.action
            val label = if (action == QuickActionType.FLASHLIGHT && QuickActionsFlashlight.isOn(context)) {
                context.getString(R.string.widget_quick_actions_flash_on)
            } else {
                action.label
            }
            val icon = if (action == QuickActionType.FLASHLIGHT && QuickActionsFlashlight.isOn(context)) {
                R.drawable.quick_actions_ic_flashlight_on
            } else {
                action.iconRes
            }

            views.setImageViewResource(slotUi.iconId, icon)
            views.setTextViewText(slotUi.labelId, label)
            if (interactive) {
                views.setOnClickPendingIntent(
                    slotUi.rootId,
                    runPendingIntent(context, action, slotUi.requestCode),
                )
            }
        }
        return views
    }

    private fun runPendingIntent(
        context: Context,
        action: QuickActionType,
        requestCode: Int,
    ): PendingIntent {
        val intent = Intent(context, QuickActionsWidgetReceiver::class.java).apply {
            this.action = QuickActionsConfig.ACTION_RUN
            putExtra(QuickActionsConfig.EXTRA_ACTION, action.key)
        }
        return PendingIntent.getBroadcast(
            context.applicationContext,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
