package com.phoenix.nothingwidget.widgets.device_health

import android.content.Context
import com.phoenix.nothingwidget.core.widget_state.PageStateRepository

/** Page index 0..3 set only by ◀ ▶ / dots (no swipe). */
object DeviceHealthPageStore {

    private const val LEGACY_PREFS = "device_health_pages"

    val PAGE_IDS = listOf(
        PAGE_ID_BATTERY,
        PAGE_ID_CHARGING,
        PAGE_ID_DETAILS,
        PAGE_ID_PERFORMANCE,
    )

    const val PAGE_ID_BATTERY = "battery"
    const val PAGE_ID_CHARGING = "charging"
    const val PAGE_ID_DETAILS = "details"
    const val PAGE_ID_PERFORMANCE = "performance"

    fun getPage(context: Context, appWidgetId: Int): Int {
        migrateLegacyIfNeeded(context, appWidgetId)
        return PageStateRepository.getPageIndex(
            context = context,
            widgetType = DeviceHealthConfig.WIDGET_ID,
            appWidgetId = appWidgetId,
            pageIds = PAGE_IDS,
            defaultIndex = DeviceHealthConfig.PAGE_OVERVIEW,
        )
    }

    fun setPage(context: Context, appWidgetId: Int, page: Int) {
        PageStateRepository.setPageIndex(
            context = context,
            widgetType = DeviceHealthConfig.WIDGET_ID,
            appWidgetId = appWidgetId,
            pageIds = PAGE_IDS,
            pageIndex = page.coerceIn(0, DeviceHealthConfig.PAGE_COUNT - 1),
        )
    }

    fun adjacent(page: Int, delta: Int): Int {
        val count = DeviceHealthConfig.PAGE_COUNT
        return ((page + delta) % count + count) % count
    }

    /** One-time bridge from the old SharedPreferences page index. */
    private fun migrateLegacyIfNeeded(context: Context, appWidgetId: Int) {
        if (PageStateRepository.getInstancePageId(context, DeviceHealthConfig.WIDGET_ID, appWidgetId) != null) {
            return
        }
        val prefs = context.applicationContext.getSharedPreferences(LEGACY_PREFS, Context.MODE_PRIVATE)
        if (!prefs.contains("page_$appWidgetId")) return
        val legacy = prefs.getInt("page_$appWidgetId", DeviceHealthConfig.PAGE_OVERVIEW)
            .coerceIn(0, DeviceHealthConfig.PAGE_COUNT - 1)
        setPage(context, appWidgetId, legacy)
        prefs.edit().remove("page_$appWidgetId").apply()
    }
}
