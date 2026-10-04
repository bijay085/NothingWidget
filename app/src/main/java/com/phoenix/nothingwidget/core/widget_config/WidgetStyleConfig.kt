package com.phoenix.nothingwidget.core.widget_config

import androidx.annotation.ColorInt

/** Shared choice types used by element and background pickers. */
sealed interface StyleChoice {
    val label: String
}

data class ColorChoice(
    override val label: String,
    @param:ColorInt val color: Int,
) : StyleChoice

data class FontChoice(
    override val label: String,
    val key: String,
) : StyleChoice
