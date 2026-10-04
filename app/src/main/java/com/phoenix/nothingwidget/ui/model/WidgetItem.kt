package com.phoenix.nothingwidget.ui.model

import androidx.annotation.DrawableRes
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomization
import com.phoenix.nothingwidget.ui.data.WidgetMetadata
import java.util.concurrent.TimeUnit

data class WidgetItem(
    val id: String,
    val name: String,
    val category: WidgetCategory,
    val metadata: WidgetMetadata,
    val addedAtMillis: Long,
    val providerClassName: String,
    @param:DrawableRes val previewResId: Int,
    val previewCircular: Boolean = false,
    val customization: WidgetCustomization? = null,
) {
    val description: String get() = metadata.description

    fun isRecentlyAdded(nowMillis: Long = System.currentTimeMillis()): Boolean {
        return nowMillis - addedAtMillis <= RECENT_WINDOW_MS
    }

    /**
     * Card tags in a fixed order: size → Fixed/Resizable → feature → New.
     * Category names and static "New" from meta are stripped (New is dynamic).
     */
    fun resolvedTags(nowMillis: Long = System.currentTimeMillis()): List<String> {
        val blocked = WidgetCategory.entries.map { it.label.lowercase() }.toSet() + setOf("new")
        val raw = metadata.tags
            .map { it.trim() }
            .filter { it.isNotEmpty() && it.lowercase() !in blocked }
            .distinct()

        val size = raw.filter { SIZE_TAG.matches(it) }
        val resize = raw.filter { it.equals("Fixed", true) || it.equals("Resizable", true) }
            .map { tag ->
                if (tag.equals("Fixed", true)) "Fixed" else "Resizable"
            }
        val features = raw.filter { tag ->
            !SIZE_TAG.matches(tag) &&
                !tag.equals("Fixed", true) &&
                !tag.equals("Resizable", true)
        }

        return buildList {
            addAll(size)
            addAll(resize)
            addAll(features)
            if (isRecentlyAdded(nowMillis)) add("New")
        }.distinct().take(MAX_TAGS)
    }

    companion object {
        val RECENT_WINDOW_MS: Long = TimeUnit.DAYS.toMillis(10)
        private const val MAX_TAGS = 3
        /** `2×2`, `4×2+`, `4x3` — allow × or x. */
        private val SIZE_TAG = Regex("""^\d+[×x]\d+\+?$""")
    }
}
