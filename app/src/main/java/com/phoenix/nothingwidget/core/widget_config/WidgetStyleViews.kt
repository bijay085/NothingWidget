package com.phoenix.nothingwidget.core.widget_config

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes

/** RemoteViews helpers shared by every widget renderer. */

fun isTransparent(@ColorInt color: Int): Boolean = Color.alpha(color) == 0

/** Swap the whole background drawable — more reliable than tint for solid presets. */
fun RemoteViews.setBackgroundRes(viewId: Int, @DrawableRes resId: Int) {
    setInt(viewId, "setBackgroundResource", resId)
}

/**
 * Apply a solid background: known presets keep their drawables; anything else
 * (Custom picker) tints a white rounded/oval base so corner shape is preserved.
 */
fun RemoteViews.setSolidBackground(
    viewId: Int,
    @ColorInt color: Int,
    @DrawableRes tintableBaseRes: Int,
    presetResFor: (Int) -> Int? = { null },
    @DrawableRes transparentRes: Int? = null,
) {
    if (transparentRes != null && isTransparent(color)) {
        setBackgroundRes(viewId, transparentRes)
        return
    }
    val preset = presetResFor(color)
    if (preset != null) {
        setBackgroundRes(viewId, preset)
        return
    }
    setBackgroundRes(viewId, tintableBaseRes)
    setColorStateList(viewId, "setBackgroundTintList", ColorStateList.valueOf(color))
}

/** Recolors an ImageView, preserving per-pixel alpha (glows, gradients). */
fun RemoteViews.tintImage(viewId: Int, @ColorInt color: Int) {
    setInt(viewId, "setColorFilter", color)
}

fun RemoteViews.setTextColors(@ColorInt color: Int, vararg viewIds: Int) {
    viewIds.forEach { id -> setTextColor(id, color) }
}

fun RemoteViews.setTextSizePx(sizePx: Float, vararg viewIds: Int) {
    viewIds.forEach { id -> setTextViewTextSize(id, TypedValue.COMPLEX_UNIT_PX, sizePx) }
}

/** Font variants are sibling views with different XML fontFamily; show only the selected one. */
fun RemoteViews.showFontVariant(fontKey: String, variants: Map<String, Int>, fallbackKey: String) {
    val selected = variants[fontKey] ?: variants.getValue(fallbackKey)
    variants.values.forEach { id ->
        setViewVisibility(id, if (id == selected) View.VISIBLE else View.GONE)
    }
}
