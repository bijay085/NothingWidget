package com.phoenix.nothingwidget.widgets.screen_time_large

import android.content.Context
import com.phoenix.nothingwidget.core.widget_state.PageStateRepository

/** 0 = Screen Time, 1 = Privacy. */
object ScreenTimeLargePageStore {

    val PAGE_IDS = listOf(
        PAGE_ID_TODAY,
        PAGE_ID_PRIVACY,
    )

    const val PAGE_ID_TODAY = "today"
    const val PAGE_ID_PRIVACY = "privacy"

    fun getPage(context: Context, appWidgetId: Int): Int =
        PageStateRepository.getPageIndex(
            context = context,
            widgetType = ScreenTimeLargeConfig.WIDGET_ID,
            appWidgetId = appWidgetId,
            pageIds = PAGE_IDS,
            defaultIndex = ScreenTimeLargeConfig.PAGE_TODAY,
        )

    fun setPage(context: Context, appWidgetId: Int, page: Int) {
        PageStateRepository.setPageIndex(
            context = context,
            widgetType = ScreenTimeLargeConfig.WIDGET_ID,
            appWidgetId = appWidgetId,
            pageIds = PAGE_IDS,
            pageIndex = page.coerceIn(0, ScreenTimeLargeConfig.PAGE_COUNT - 1),
        )
    }

    fun adjacent(page: Int, delta: Int): Int {
        val count = ScreenTimeLargeConfig.PAGE_COUNT
        return ((page + delta) % count + count) % count
    }
}
