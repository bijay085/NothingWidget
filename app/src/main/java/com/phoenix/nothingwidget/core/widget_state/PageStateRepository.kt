package com.phoenix.nothingwidget.core.widget_state

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.widgetPageStateDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "widget_page_state",
)

/**
 * Shared last-page memory for multi-page home-screen widgets.
 *
 * Keys:
 * - `{widgetType}_widget_{appWidgetId}` → page id (per instance)
 * - `{widgetType}_widget_last` → page id (type-level, survives remove / re-add)
 *
 * Values are stable page ids such as `privacy` or `performance`, not flipper indices.
 */
object PageStateRepository {

    /** Blocking read for AppWidgetProvider / RemoteViews paths. */
    fun getPageId(context: Context, widgetType: String, appWidgetId: Int): String? =
        runBlocking { getPageIdSuspend(context, widgetType, appWidgetId) }

    /** Instance key only (no type-level fallback). Used for legacy migration. */
    fun getInstancePageId(context: Context, widgetType: String, appWidgetId: Int): String? =
        runBlocking {
            val prefs = context.applicationContext.widgetPageStateDataStore.data.first()
            prefs[instanceKey(widgetType, appWidgetId)]?.trim()?.takeIf { it.isNotEmpty() }
        }

    /** Blocking write for ◀ ▶ / dot PendingIntent handlers. */
    fun setPageId(context: Context, widgetType: String, appWidgetId: Int, pageId: String) {
        runBlocking { setPageIdSuspend(context, widgetType, appWidgetId, pageId) }
    }

    /**
     * Resolve a flipper index from stored page ids.
     * Order: instance key → type-level last → [defaultIndex].
     */
    fun getPageIndex(
        context: Context,
        widgetType: String,
        appWidgetId: Int,
        pageIds: List<String>,
        defaultIndex: Int = 0,
    ): Int {
        require(pageIds.isNotEmpty()) { "pageIds must not be empty" }
        val fallback = defaultIndex.coerceIn(0, pageIds.lastIndex)
        val saved = getPageId(context, widgetType, appWidgetId) ?: return fallback
        val index = pageIds.indexOf(saved)
        return if (index >= 0) index else fallback
    }

    fun setPageIndex(
        context: Context,
        widgetType: String,
        appWidgetId: Int,
        pageIds: List<String>,
        pageIndex: Int,
    ) {
        require(pageIds.isNotEmpty()) { "pageIds must not be empty" }
        val index = pageIndex.coerceIn(0, pageIds.lastIndex)
        setPageId(context, widgetType, appWidgetId, pageIds[index])
    }

    suspend fun getPageIdSuspend(
        context: Context,
        widgetType: String,
        appWidgetId: Int,
    ): String? {
        val prefs = context.applicationContext.widgetPageStateDataStore.data.first()
        val instance = prefs[instanceKey(widgetType, appWidgetId)]?.trim()?.takeIf { it.isNotEmpty() }
        if (instance != null) return instance
        return prefs[lastKey(widgetType)]?.trim()?.takeIf { it.isNotEmpty() }
    }

    suspend fun setPageIdSuspend(
        context: Context,
        widgetType: String,
        appWidgetId: Int,
        pageId: String,
    ) {
        val safe = pageId.trim()
        if (safe.isEmpty()) return
        context.applicationContext.widgetPageStateDataStore.edit { prefs ->
            prefs[instanceKey(widgetType, appWidgetId)] = safe
            prefs[lastKey(widgetType)] = safe
        }
    }

    private fun instanceKey(widgetType: String, appWidgetId: Int) =
        stringPreferencesKey("${widgetType}_widget_$appWidgetId")

    private fun lastKey(widgetType: String) =
        stringPreferencesKey("${widgetType}_widget_last")
}
