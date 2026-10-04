package com.phoenix.nothingwidget.widgets.screen_time

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import com.phoenix.nothingwidget.R
import com.phoenix.nothingwidget.core.widget_config.ElementStyleConfig
import com.phoenix.nothingwidget.core.widget_config.StylePresets
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomizationConfig
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomizationRepository
import com.phoenix.nothingwidget.core.widget_config.setSolidBackground
import com.phoenix.nothingwidget.core.widget_config.setTextSizePx
import com.phoenix.nothingwidget.core.widget_config.showFontVariant
import com.phoenix.nothingwidget.core.widget_config.tintImage
object ScreenTimeWidgetRenderer {

    private val TOTAL_VARIANTS = mapOf(
        StylePresets.FONT_CLEAN to R.id.screen_time_total,
        StylePresets.FONT_DEFAULT to R.id.screen_time_total,
        StylePresets.FONT_DIGITAL to R.id.screen_time_total_digital,
        StylePresets.FONT_FUTURISTIC to R.id.screen_time_total_futuristic,
        StylePresets.FONT_PIXEL_MONO to R.id.screen_time_total_pixel,
        StylePresets.FONT_DARK_SPOOKY to R.id.screen_time_total_spooky,
    )

    private val LABEL_VARIANTS = mapOf(
        StylePresets.FONT_CLEAN to R.id.screen_time_label,
        StylePresets.FONT_DEFAULT to R.id.screen_time_label,
        StylePresets.FONT_DIGITAL to R.id.screen_time_label_digital,
        StylePresets.FONT_FUTURISTIC to R.id.screen_time_label_futuristic,
        StylePresets.FONT_PIXEL_MONO to R.id.screen_time_label_pixel,
        StylePresets.FONT_DARK_SPOOKY to R.id.screen_time_label_spooky,
    )

    private val APP_VARIANTS = mapOf(
        StylePresets.FONT_CLEAN to R.id.screen_time_top_app,
        StylePresets.FONT_DEFAULT to R.id.screen_time_top_app,
        StylePresets.FONT_DIGITAL to R.id.screen_time_top_app_digital,
        StylePresets.FONT_FUTURISTIC to R.id.screen_time_top_app_futuristic,
        StylePresets.FONT_PIXEL_MONO to R.id.screen_time_top_app_pixel,
        StylePresets.FONT_DARK_SPOOKY to R.id.screen_time_top_app_spooky,
    )

    fun renderAll(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(
            ComponentName(context, ScreenTimeWidgetReceiver::class.java),
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
        val model = ScreenTimeRepository.loadToday(context)
        val config = WidgetCustomizationRepository.config(context, ScreenTimeCustomization)
        appWidgetIds.forEach { id ->
            appWidgetManager.updateAppWidget(id, build(context, model, config, interactive = true))
        }
    }

    /** Live customization preview — fixed sample content, styled by [config]. */
    fun preview(context: Context, config: WidgetCustomizationConfig): RemoteViews {
        val sample = ScreenTimeModel(
            totalScreenTimeToday = (5 * 60 + 42) * 60_000L,
            topAppName = "Instagram",
            topAppUsageTime = (2 * 60 + 10) * 60_000L,
            hasPermission = true,
        )
        return build(context, sample, config, interactive = false)
    }

    private fun build(
        context: Context,
        model: ScreenTimeModel,
        config: WidgetCustomizationConfig,
        interactive: Boolean,
    ): RemoteViews {
        val total = config.element(ScreenTimeCustomization.ELEMENT_TOTAL)
        val label = config.element(ScreenTimeCustomization.ELEMENT_LABEL)
        val app = config.element(ScreenTimeCustomization.ELEMENT_APP)
        val indicator = config.element(ScreenTimeCustomization.ELEMENT_INDICATOR)

        val views = RemoteViews(context.packageName, R.layout.widget_screen_time)
        views.setSolidBackground(
            viewId = R.id.widget_screen_time_root,
            color = config.backgroundColor,
            tintableBaseRes = R.drawable.screen_time_background_tintable,
            presetResFor = { color ->
                if (color == ScreenTimeCustomization.COLOR_BACKGROUND) {
                    R.drawable.screen_time_background
                } else {
                    null
                }
            },
        )

        views.showFontVariant(total.fontFamily, TOTAL_VARIANTS, StylePresets.FONT_CLEAN)
        views.showFontVariant(label.fontFamily, LABEL_VARIANTS, StylePresets.FONT_CLEAN)
        views.showFontVariant(app.fontFamily, APP_VARIANTS, StylePresets.FONT_CLEAN)

        applyText(views, context, TOTAL_VARIANTS, model.totalScreenTimeLabel, total)
        applyText(views, context, LABEL_VARIANTS, "Today", label)
        applyText(
            views,
            context,
            APP_VARIANTS,
            if (model.hasPermission) model.topAppName else "Grant usage access",
            app,
        )

        // Existing bottom TextView: top-app duration (not a calendar date).
        views.setTextViewText(
            R.id.screen_time_date,
            if (model.hasPermission) model.topAppUsageLabel else "",
        )
        views.setTextColor(R.id.screen_time_date, app.textColor)
        applyIndicator(views, indicator)

        if (interactive) {
            views.setOnClickPendingIntent(
                R.id.widget_screen_time_root,
                ScreenTimeClickHelper.pendingIntent(context),
            )
        }
        return views
    }

    private fun applyText(
        views: RemoteViews,
        context: Context,
        variants: Map<String, Int>,
        text: String,
        style: ElementStyleConfig,
    ) {
        val sizePx = sp(context, style.fontSize)
        variants.values.toSet().forEach { id ->
            views.setTextViewText(id, text)
            views.setTextColor(id, style.textColor)
            views.setTextSizePx(sizePx, id)
        }
    }

    private fun applyIndicator(views: RemoteViews, indicator: ElementStyleConfig) {
        val style = indicator.iconStyle ?: ScreenTimeCustomization.INDICATOR_NONE
        val showRing = style == ScreenTimeCustomization.INDICATOR_RING
        val showBar = style == ScreenTimeCustomization.INDICATOR_BAR
        views.setViewVisibility(
            R.id.screen_time_indicator_ring,
            if (showRing) View.VISIBLE else View.GONE,
        )
        views.setViewVisibility(
            R.id.screen_time_indicator_bar,
            if (showBar) View.VISIBLE else View.GONE,
        )
        if (showRing) views.tintImage(R.id.screen_time_indicator_ring, indicator.textColor)
        if (showBar) views.tintImage(R.id.screen_time_indicator_bar, indicator.textColor)
    }

    private fun sp(context: Context, sizeSp: Int): Float =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sizeSp.toFloat(),
            context.resources.displayMetrics,
        )
}
