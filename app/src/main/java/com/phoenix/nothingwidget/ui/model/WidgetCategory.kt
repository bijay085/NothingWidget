package com.phoenix.nothingwidget.ui.model

/**
 * Widget type categories used by top filters.
 * Tabs with no widgets stay hidden until the catalog includes them.
 */
enum class WidgetCategory(val label: String) {
    Clock("Clock"),
    Weather("Weather"),
    Battery("Battery"),
    Calendar("Calendar"),
}

/** Category navigation only — never Recently Added / Favorites. */
sealed class LibraryFilter {
    abstract val label: String

    data object All : LibraryFilter() {
        override val label: String = "All"
    }

    data class Category(val category: WidgetCategory) : LibraryFilter() {
        override val label: String get() = category.label
    }

    companion object {
        fun tabsFor(catalog: List<WidgetItem>): List<LibraryFilter> {
            val present = catalog.map { it.category }.toSet()
            return buildList {
                add(All)
                WidgetCategory.entries.forEach { category ->
                    if (category in present) add(Category(category))
                }
            }
        }
    }
}

/**
 * App-level destinations outside the category home screen.
 * Not shown as filters or home shortcuts.
 */
sealed class AppDestination {
    data object Library : AppDestination()
    data object RecentlyAdded : AppDestination()
    data object Favorites : AppDestination()
    data object Settings : AppDestination()
    data class Customize(val widgetId: String) : AppDestination()
}
