package com.phoenix.nothingwidget.ui.model

import androidx.annotation.DrawableRes
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomization
import java.util.concurrent.TimeUnit

data class WidgetItem(
    val id: String,
    val name: String,
    val category: WidgetCategory,
    val tags: List<String>,
    val addedAtMillis: Long,
    val providerClassName: String,
    @param:DrawableRes val previewResId: Int,
    val previewCircular: Boolean = false,
    val customization: WidgetCustomization? = null,
) {
    fun isRecentlyAdded(nowMillis: Long = System.currentTimeMillis()): Boolean {
        return nowMillis - addedAtMillis <= RECENT_WINDOW_MS
    }

    /**
     * Display tags only — never repeats the category label.
     * "New" is added dynamically for recently added widgets.
     */
    fun resolvedTags(nowMillis: Long = System.currentTimeMillis()): List<String> {
        val categoryLabels = WidgetCategory.entries.map { it.label.lowercase() }.toSet()
        return buildList {
            tags.forEach { tag ->
                if (tag.lowercase() !in categoryLabels) add(tag)
            }
            if (isRecentlyAdded(nowMillis)) add("New")
        }.distinct()
    }

    companion object {
        val RECENT_WINDOW_MS: Long = TimeUnit.DAYS.toMillis(10)
    }
}
