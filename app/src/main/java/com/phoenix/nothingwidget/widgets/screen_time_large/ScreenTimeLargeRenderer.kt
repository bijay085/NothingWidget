package com.phoenix.nothingwidget.widgets.screen_time_large

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
import com.phoenix.nothingwidget.widgets.screen_time.ScreenTimeClickHelper
import com.phoenix.nothingwidget.widgets.screen_time.ScreenTimeModel
import com.phoenix.nothingwidget.widgets.screen_time.ScreenTimeRepository
import com.phoenix.nothingwidget.widgets.screen_time.TopAppUsageItem

/**
 * RemoteViews renderer for Screen Time Large.
 * Visible app rows scale with widget height (resize → 2…6 apps).
 */
object ScreenTimeLargeRenderer {

    private val TOTAL_VARIANTS = mapOf(
        StylePresets.FONT_CLEAN to R.id.screen_time_large_total,
        StylePresets.FONT_DEFAULT to R.id.screen_time_large_total,
        StylePresets.FONT_DIGITAL to R.id.screen_time_large_total_digital,
        StylePresets.FONT_FUTURISTIC to R.id.screen_time_large_total_futuristic,
        StylePresets.FONT_PIXEL_MONO to R.id.screen_time_large_total_pixel,
        StylePresets.FONT_DARK_SPOOKY to R.id.screen_time_large_total_spooky,
    )

    private val LABEL_VARIANTS = mapOf(
        StylePresets.FONT_CLEAN to R.id.screen_time_large_label,
        StylePresets.FONT_DEFAULT to R.id.screen_time_large_label,
        StylePresets.FONT_DIGITAL to R.id.screen_time_large_label_digital,
        StylePresets.FONT_FUTURISTIC to R.id.screen_time_large_label_futuristic,
        StylePresets.FONT_PIXEL_MONO to R.id.screen_time_large_label_pixel,
        StylePresets.FONT_DARK_SPOOKY to R.id.screen_time_large_label_spooky,
    )

    private val SECTION_VARIANTS = mapOf(
        StylePresets.FONT_CLEAN to R.id.screen_time_large_section,
        StylePresets.FONT_DEFAULT to R.id.screen_time_large_section,
        StylePresets.FONT_DIGITAL to R.id.screen_time_large_section_digital,
        StylePresets.FONT_FUTURISTIC to R.id.screen_time_large_section_futuristic,
        StylePresets.FONT_PIXEL_MONO to R.id.screen_time_large_section_pixel,
        StylePresets.FONT_DARK_SPOOKY to R.id.screen_time_large_section_spooky,
    )

    private data class AppRow(
        val rowId: Int,
        val nameVariants: Map<String, Int>,
        val timeVariants: Map<String, Int>,
    )

    private val APP_ROWS: List<AppRow> = listOf(
        appRow(
            rowId = R.id.screen_time_large_app1_row,
            nameClean = R.id.screen_time_large_app1_name,
            nameDigital = R.id.screen_time_large_app1_name_digital,
            nameFuturistic = R.id.screen_time_large_app1_name_futuristic,
            namePixel = R.id.screen_time_large_app1_name_pixel,
            nameSpooky = R.id.screen_time_large_app1_name_spooky,
            timeClean = R.id.screen_time_large_app1_time,
            timeDigital = R.id.screen_time_large_app1_time_digital,
            timeFuturistic = R.id.screen_time_large_app1_time_futuristic,
            timePixel = R.id.screen_time_large_app1_time_pixel,
            timeSpooky = R.id.screen_time_large_app1_time_spooky,
        ),
        appRow(
            rowId = R.id.screen_time_large_app2_row,
            nameClean = R.id.screen_time_large_app2_name,
            nameDigital = R.id.screen_time_large_app2_name_digital,
            nameFuturistic = R.id.screen_time_large_app2_name_futuristic,
            namePixel = R.id.screen_time_large_app2_name_pixel,
            nameSpooky = R.id.screen_time_large_app2_name_spooky,
            timeClean = R.id.screen_time_large_app2_time,
            timeDigital = R.id.screen_time_large_app2_time_digital,
            timeFuturistic = R.id.screen_time_large_app2_time_futuristic,
            timePixel = R.id.screen_time_large_app2_time_pixel,
            timeSpooky = R.id.screen_time_large_app2_time_spooky,
        ),
        appRow(
            rowId = R.id.screen_time_large_app3_row,
            nameClean = R.id.screen_time_large_app3_name,
            nameDigital = R.id.screen_time_large_app3_name_digital,
            nameFuturistic = R.id.screen_time_large_app3_name_futuristic,
            namePixel = R.id.screen_time_large_app3_name_pixel,
            nameSpooky = R.id.screen_time_large_app3_name_spooky,
            timeClean = R.id.screen_time_large_app3_time,
            timeDigital = R.id.screen_time_large_app3_time_digital,
            timeFuturistic = R.id.screen_time_large_app3_time_futuristic,
            timePixel = R.id.screen_time_large_app3_time_pixel,
            timeSpooky = R.id.screen_time_large_app3_time_spooky,
        ),
        appRow(
            rowId = R.id.screen_time_large_app4_row,
            nameClean = R.id.screen_time_large_app4_name,
            nameDigital = R.id.screen_time_large_app4_name_digital,
            nameFuturistic = R.id.screen_time_large_app4_name_futuristic,
            namePixel = R.id.screen_time_large_app4_name_pixel,
            nameSpooky = R.id.screen_time_large_app4_name_spooky,
            timeClean = R.id.screen_time_large_app4_time,
            timeDigital = R.id.screen_time_large_app4_time_digital,
            timeFuturistic = R.id.screen_time_large_app4_time_futuristic,
            timePixel = R.id.screen_time_large_app4_time_pixel,
            timeSpooky = R.id.screen_time_large_app4_time_spooky,
        ),
        appRow(
            rowId = R.id.screen_time_large_app5_row,
            nameClean = R.id.screen_time_large_app5_name,
            nameDigital = R.id.screen_time_large_app5_name_digital,
            nameFuturistic = R.id.screen_time_large_app5_name_futuristic,
            namePixel = R.id.screen_time_large_app5_name_pixel,
            nameSpooky = R.id.screen_time_large_app5_name_spooky,
            timeClean = R.id.screen_time_large_app5_time,
            timeDigital = R.id.screen_time_large_app5_time_digital,
            timeFuturistic = R.id.screen_time_large_app5_time_futuristic,
            timePixel = R.id.screen_time_large_app5_time_pixel,
            timeSpooky = R.id.screen_time_large_app5_time_spooky,
        ),
        appRow(
            rowId = R.id.screen_time_large_app6_row,
            nameClean = R.id.screen_time_large_app6_name,
            nameDigital = R.id.screen_time_large_app6_name_digital,
            nameFuturistic = R.id.screen_time_large_app6_name_futuristic,
            namePixel = R.id.screen_time_large_app6_name_pixel,
            nameSpooky = R.id.screen_time_large_app6_name_spooky,
            timeClean = R.id.screen_time_large_app6_time,
            timeDigital = R.id.screen_time_large_app6_time_digital,
            timeFuturistic = R.id.screen_time_large_app6_time_futuristic,
            timePixel = R.id.screen_time_large_app6_time_pixel,
            timeSpooky = R.id.screen_time_large_app6_time_spooky,
        ),
        appRow(
            rowId = R.id.screen_time_large_app7_row,
            nameClean = R.id.screen_time_large_app7_name,
            nameDigital = R.id.screen_time_large_app7_name_digital,
            nameFuturistic = R.id.screen_time_large_app7_name_futuristic,
            namePixel = R.id.screen_time_large_app7_name_pixel,
            nameSpooky = R.id.screen_time_large_app7_name_spooky,
            timeClean = R.id.screen_time_large_app7_time,
            timeDigital = R.id.screen_time_large_app7_time_digital,
            timeFuturistic = R.id.screen_time_large_app7_time_futuristic,
            timePixel = R.id.screen_time_large_app7_time_pixel,
            timeSpooky = R.id.screen_time_large_app7_time_spooky,
        ),
        appRow(
            rowId = R.id.screen_time_large_app8_row,
            nameClean = R.id.screen_time_large_app8_name,
            nameDigital = R.id.screen_time_large_app8_name_digital,
            nameFuturistic = R.id.screen_time_large_app8_name_futuristic,
            namePixel = R.id.screen_time_large_app8_name_pixel,
            nameSpooky = R.id.screen_time_large_app8_name_spooky,
            timeClean = R.id.screen_time_large_app8_time,
            timeDigital = R.id.screen_time_large_app8_time_digital,
            timeFuturistic = R.id.screen_time_large_app8_time_futuristic,
            timePixel = R.id.screen_time_large_app8_time_pixel,
            timeSpooky = R.id.screen_time_large_app8_time_spooky,
        ),
        appRow(
            rowId = R.id.screen_time_large_app9_row,
            nameClean = R.id.screen_time_large_app9_name,
            nameDigital = R.id.screen_time_large_app9_name_digital,
            nameFuturistic = R.id.screen_time_large_app9_name_futuristic,
            namePixel = R.id.screen_time_large_app9_name_pixel,
            nameSpooky = R.id.screen_time_large_app9_name_spooky,
            timeClean = R.id.screen_time_large_app9_time,
            timeDigital = R.id.screen_time_large_app9_time_digital,
            timeFuturistic = R.id.screen_time_large_app9_time_futuristic,
            timePixel = R.id.screen_time_large_app9_time_pixel,
            timeSpooky = R.id.screen_time_large_app9_time_spooky,
        ),
        appRow(
            rowId = R.id.screen_time_large_app10_row,
            nameClean = R.id.screen_time_large_app10_name,
            nameDigital = R.id.screen_time_large_app10_name_digital,
            nameFuturistic = R.id.screen_time_large_app10_name_futuristic,
            namePixel = R.id.screen_time_large_app10_name_pixel,
            nameSpooky = R.id.screen_time_large_app10_name_spooky,
            timeClean = R.id.screen_time_large_app10_time,
            timeDigital = R.id.screen_time_large_app10_time_digital,
            timeFuturistic = R.id.screen_time_large_app10_time_futuristic,
            timePixel = R.id.screen_time_large_app10_time_pixel,
            timeSpooky = R.id.screen_time_large_app10_time_spooky,
        ),
        appRow(
            rowId = R.id.screen_time_large_app11_row,
            nameClean = R.id.screen_time_large_app11_name,
            nameDigital = R.id.screen_time_large_app11_name_digital,
            nameFuturistic = R.id.screen_time_large_app11_name_futuristic,
            namePixel = R.id.screen_time_large_app11_name_pixel,
            nameSpooky = R.id.screen_time_large_app11_name_spooky,
            timeClean = R.id.screen_time_large_app11_time,
            timeDigital = R.id.screen_time_large_app11_time_digital,
            timeFuturistic = R.id.screen_time_large_app11_time_futuristic,
            timePixel = R.id.screen_time_large_app11_time_pixel,
            timeSpooky = R.id.screen_time_large_app11_time_spooky,
        ),
        appRow(
            rowId = R.id.screen_time_large_app12_row,
            nameClean = R.id.screen_time_large_app12_name,
            nameDigital = R.id.screen_time_large_app12_name_digital,
            nameFuturistic = R.id.screen_time_large_app12_name_futuristic,
            namePixel = R.id.screen_time_large_app12_name_pixel,
            nameSpooky = R.id.screen_time_large_app12_name_spooky,
            timeClean = R.id.screen_time_large_app12_time,
            timeDigital = R.id.screen_time_large_app12_time_digital,
            timeFuturistic = R.id.screen_time_large_app12_time_futuristic,
            timePixel = R.id.screen_time_large_app12_time_pixel,
            timeSpooky = R.id.screen_time_large_app12_time_spooky,
        ),
    )

    private fun appRow(
        rowId: Int,
        nameClean: Int,
        nameDigital: Int,
        nameFuturistic: Int,
        namePixel: Int,
        nameSpooky: Int,
        timeClean: Int,
        timeDigital: Int,
        timeFuturistic: Int,
        timePixel: Int,
        timeSpooky: Int,
    ): AppRow = AppRow(
        rowId = rowId,
        nameVariants = mapOf(
            StylePresets.FONT_CLEAN to nameClean,
            StylePresets.FONT_DEFAULT to nameClean,
            StylePresets.FONT_DIGITAL to nameDigital,
            StylePresets.FONT_FUTURISTIC to nameFuturistic,
            StylePresets.FONT_PIXEL_MONO to namePixel,
            StylePresets.FONT_DARK_SPOOKY to nameSpooky,
        ),
        timeVariants = mapOf(
            StylePresets.FONT_CLEAN to timeClean,
            StylePresets.FONT_DEFAULT to timeClean,
            StylePresets.FONT_DIGITAL to timeDigital,
            StylePresets.FONT_FUTURISTIC to timeFuturistic,
            StylePresets.FONT_PIXEL_MONO to timePixel,
            StylePresets.FONT_DARK_SPOOKY to timeSpooky,
        ),
    )

    fun renderAll(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(
            ComponentName(context, ScreenTimeLargeWidgetReceiver::class.java),
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
        val config = WidgetCustomizationRepository.config(context, ScreenTimeLargeCustomization)
        appWidgetIds.forEach { id ->
            val maxApps = visibleAppCount(appWidgetManager, id)
            appWidgetManager.updateAppWidget(
                id,
                build(context, model, config, interactive = true, maxApps = maxApps),
            )
        }
    }

    fun preview(context: Context, config: WidgetCustomizationConfig): RemoteViews {
        val sample = ScreenTimeModel(
            totalScreenTimeToday = (4 * 60 + 14) * 60_000L,
            topAppName = "Instagram",
            topAppUsageTime = (1 * 60 + 56) * 60_000L,
            hasPermission = true,
            topApps = listOf(
                TopAppUsageItem("Nothing Widget", (2 * 60 + 6) * 60_000L, "com.phoenix.nothingwidget"),
                TopAppUsageItem("Instagram", (1 * 60 + 56) * 60_000L, "com.instagram.android"),
                TopAppUsageItem("Settings", 9 * 60_000L, "com.android.settings"),
                TopAppUsageItem("YouTube", 7 * 60_000L, "com.google.android.youtube"),
                TopAppUsageItem("Chrome", 5 * 60_000L, "com.android.chrome"),
                TopAppUsageItem("Messages", 3 * 60_000L, "com.google.android.apps.messaging"),
                TopAppUsageItem("Telegram", 3 * 60_000L, "org.telegram.messenger"),
                TopAppUsageItem("Phone", 2 * 60_000L, "com.android.dialer"),
                TopAppUsageItem("Photos", 2 * 60_000L, "com.google.android.apps.photos"),
                TopAppUsageItem("Clock", 1 * 60_000L, "com.android.deskclock"),
                TopAppUsageItem("Maps", 1 * 60_000L, "com.google.android.apps.maps"),
                TopAppUsageItem("Files", 1 * 60_000L, "com.google.android.apps.nbu.files"),
            ),
        )
        return build(
            context,
            sample,
            config,
            interactive = false,
            maxApps = ScreenTimeLargeConfig.TOP_APPS_COUNT,
        )
    }

    /**
     * Map widget height (dp) to Top Apps rows.
     * Uses max(min,max) height so launchers that report a low min still fill the cell.
     * 4×2 → 2 | 4×3 → 4 | 4×4 → 6 | 4×5 → 9 | 4×6+ → 12
     */
    fun visibleAppCount(appWidgetManager: AppWidgetManager, appWidgetId: Int): Int {
        val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
        val minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0)
        val maxHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 0)
        val heightDp = maxOf(minHeight, maxHeight).takeIf { it > 0 } ?: 250
        return when {
            heightDp < 140 -> 2
            heightDp < 200 -> 4
            heightDp < 250 -> 6
            heightDp < 300 -> 9
            else -> ScreenTimeLargeConfig.TOP_APPS_COUNT
        }.coerceIn(
            ScreenTimeLargeConfig.TOP_APPS_COUNT_COMPACT,
            ScreenTimeLargeConfig.TOP_APPS_COUNT,
        )
    }

    private fun build(
        context: Context,
        model: ScreenTimeModel,
        config: WidgetCustomizationConfig,
        interactive: Boolean,
        maxApps: Int,
    ): RemoteViews {
        val total = config.element(ScreenTimeLargeCustomization.ELEMENT_TOTAL)
        val label = config.element(ScreenTimeLargeCustomization.ELEMENT_LABEL)
        val section = config.element(ScreenTimeLargeCustomization.ELEMENT_SECTION)
        val app = config.element(ScreenTimeLargeCustomization.ELEMENT_APP)
        val time = config.element(ScreenTimeLargeCustomization.ELEMENT_TIME)

        val views = RemoteViews(context.packageName, R.layout.widget_screen_time_large)
        views.setSolidBackground(
            viewId = R.id.widget_screen_time_large_root,
            color = config.backgroundColor,
            tintableBaseRes = R.drawable.screen_time_large_background_tintable,
            presetResFor = { color ->
                if (color == ScreenTimeLargeCustomization.COLOR_BACKGROUND) {
                    R.drawable.screen_time_large_background
                } else {
                    null
                }
            },
        )

        views.showFontVariant(total.fontFamily, TOTAL_VARIANTS, StylePresets.FONT_CLEAN)
        views.showFontVariant(label.fontFamily, LABEL_VARIANTS, StylePresets.FONT_CLEAN)
        views.showFontVariant(section.fontFamily, SECTION_VARIANTS, StylePresets.FONT_CLEAN)

        applyText(
            views,
            context,
            TOTAL_VARIANTS,
            if (model.hasPermission) {
                model.totalScreenTimeLabel
            } else {
                context.getString(R.string.widget_screen_time_large_grant_access)
            },
            total,
        )
        applyText(
            views,
            context,
            LABEL_VARIANTS,
            context.getString(R.string.widget_screen_time_large_today),
            label,
        )
        applyText(
            views,
            context,
            SECTION_VARIANTS,
            context.getString(R.string.widget_screen_time_large_top_apps),
            section,
        )

        val apps = model.topApps
            .takeIf { model.hasPermission }
            .orEmpty()
            .take(maxApps.coerceIn(0, APP_ROWS.size))
        bindApps(views, context, apps, app, time)

        if (interactive) {
            views.setOnClickPendingIntent(
                R.id.widget_screen_time_large_root,
                ScreenTimeClickHelper.pendingIntent(context),
            )
        }
        return views
    }

    private fun bindApps(
        views: RemoteViews,
        context: Context,
        apps: List<TopAppUsageItem>,
        appStyle: ElementStyleConfig,
        timeStyle: ElementStyleConfig,
    ) {
        APP_ROWS.forEachIndexed { index, row ->
            val item = apps.getOrNull(index)
            if (item == null) {
                views.setViewVisibility(row.rowId, View.GONE)
            } else {
                views.setViewVisibility(row.rowId, View.VISIBLE)
                views.showFontVariant(appStyle.fontFamily, row.nameVariants, StylePresets.FONT_CLEAN)
                views.showFontVariant(timeStyle.fontFamily, row.timeVariants, StylePresets.FONT_CLEAN)
                applyText(views, context, row.nameVariants, item.appName, appStyle)
                applyText(views, context, row.timeVariants, item.usageLabel, timeStyle)
            }
        }
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

    private fun sp(context: Context, sizeSp: Int): Float =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sizeSp.toFloat(),
            context.resources.displayMetrics,
        )
}
