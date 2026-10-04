package com.phoenix.nothingwidget.ui.data

import android.content.Context
import androidx.annotation.XmlRes
import org.xmlpull.v1.XmlPullParser

/**
 * Card metadata that belongs to a widget rather than to the app UI.
 *
 * Tag convention (keep every widget consistent):
 * 1. Size — `2×2`, `4×2+` (× multiplication sign; `+` = resizable)
 * 2. Resize — `Fixed` or `Resizable`
 *
 * Do not put category names, feature buzzwords, or `New` in meta —
 * category is the filter tab; `New` is added dynamically when recent.
 *
 * ```xml
 * <widget description="Clock with date and alarm">
 *     <tag label="2×2" />
 *     <tag label="Fixed" />
 * </widget>
 * ```
 */
data class WidgetMetadata(
    val description: String,
    val tags: List<String>,
) {
    companion object {
        val Empty = WidgetMetadata(description = "", tags = emptyList())

        fun read(context: Context, @XmlRes xmlResId: Int): WidgetMetadata {
            var description = ""
            val tags = mutableListOf<String>()

            context.resources.getXml(xmlResId).use { parser ->
                while (parser.next() != XmlPullParser.END_DOCUMENT) {
                    if (parser.eventType != XmlPullParser.START_TAG) continue
                    when (parser.name) {
                        "widget" -> description =
                            parser.getAttributeValue(null, "description").orEmpty()

                        "tag" -> parser.getAttributeValue(null, "label")
                            ?.takeIf { it.isNotBlank() }
                            ?.let(tags::add)
                    }
                }
            }

            return WidgetMetadata(description = description, tags = tags)
        }
    }
}

private inline fun <T : android.content.res.XmlResourceParser, R> T.use(block: (T) -> R): R {
    try {
        return block(this)
    } finally {
        close()
    }
}
