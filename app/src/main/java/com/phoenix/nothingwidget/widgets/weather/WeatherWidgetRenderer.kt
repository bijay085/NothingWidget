package com.phoenix.nothingwidget.widgets.weather

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.widget.RemoteViews
import androidx.annotation.ColorInt
import com.phoenix.nothingwidget.R
import com.phoenix.nothingwidget.core.widget_config.ElementStyleConfig
import com.phoenix.nothingwidget.core.widget_config.StylePresets
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomizationConfig
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomizationRepository
import com.phoenix.nothingwidget.core.widget_config.setBackgroundRes
import com.phoenix.nothingwidget.core.widget_config.setTextSizePx
import com.phoenix.nothingwidget.core.widget_config.showFontVariant
import com.phoenix.nothingwidget.core.widget_config.tintImage

/**
 * Single Weather RemoteViews path for every home-screen update and the in-app preview.
 * Resolves temperature / unit / condition / location itself (no app-screen dependency).
 */
object WeatherWidgetRenderer {

    private const val TAG = "WEATHER_RENDER"
    private const val EMPTY_CONDITION = "—"
    private const val NO_TINT = 0x00000000

    private val TEMPERATURE_VARIANTS = mapOf(
        StylePresets.FONT_DEFAULT to R.id.weather_temperature,
        StylePresets.FONT_NDOT to R.id.weather_temperature,
        StylePresets.FONT_CLEAN to R.id.weather_temperature_clean,
        StylePresets.FONT_DIGITAL to R.id.weather_temperature_digital,
        StylePresets.FONT_FUTURISTIC to R.id.weather_temperature_futuristic,
        StylePresets.FONT_PIXEL_MONO to R.id.weather_temperature_pixel,
    )
    private val UNIT_VARIANTS = mapOf(
        StylePresets.FONT_DEFAULT to R.id.weather_unit,
        StylePresets.FONT_SANS to R.id.weather_unit,
        StylePresets.FONT_CLEAN to R.id.weather_unit_clean,
        StylePresets.FONT_DIGITAL to R.id.weather_unit_digital,
        StylePresets.FONT_FUTURISTIC to R.id.weather_unit_futuristic,
    )
    private val CONDITION_VARIANTS = mapOf(
        StylePresets.FONT_DEFAULT to R.id.weather_condition,
        StylePresets.FONT_SANS to R.id.weather_condition,
        StylePresets.FONT_CLEAN to R.id.weather_condition_clean,
        StylePresets.FONT_DIGITAL to R.id.weather_condition_digital,
        StylePresets.FONT_FUTURISTIC to R.id.weather_condition_futuristic,
        StylePresets.FONT_DARK_SPOOKY to R.id.weather_condition_spooky,
    )
    private val LOCATION_VARIANTS = mapOf(
        StylePresets.FONT_DEFAULT to R.id.weather_location,
        StylePresets.FONT_SANS to R.id.weather_location,
        StylePresets.FONT_CLEAN to R.id.weather_location_clean,
        StylePresets.FONT_DIGITAL to R.id.weather_location_digital,
        StylePresets.FONT_DARK_SPOOKY to R.id.weather_location_spooky,
    )

    private val CLICK_TARGETS = (
        listOf(
            R.id.widget_weather_click_root,
            R.id.widget_weather_root,
            R.id.weather_icon,
        ) + TEMPERATURE_VARIANTS.values + UNIT_VARIANTS.values +
            CONDITION_VARIANTS.values + LOCATION_VARIANTS.values
        ).toSet().toIntArray()

    fun render(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
        cityOverride: String? = null,
        weatherOverride: WeatherModel? = null,
    ) {
        if (appWidgetIds.isEmpty()) return
        val appContext = context.applicationContext
        val weather = resolveModel(appContext, cityOverride, weatherOverride)
        val config = WidgetCustomizationRepository.config(appContext, WeatherCustomization)
        appWidgetManager.updateAppWidget(
            appWidgetIds,
            build(appContext, weather, config, interactive = true),
        )
    }

    fun renderAll(
        context: Context,
        cityOverride: String? = null,
        weatherOverride: WeatherModel? = null,
    ) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context, WeatherWidgetReceiver::class.java))
        render(context, manager, ids, cityOverride, weatherOverride)
    }

    fun preview(context: Context, config: WidgetCustomizationConfig): RemoteViews {
        val appContext = context.applicationContext
        return build(appContext, resolveModel(appContext, null, null), config, interactive = false)
    }

    private fun resolveModel(
        context: Context,
        cityOverride: String?,
        weatherOverride: WeatherModel?,
    ): WeatherModel {
        val base = weatherOverride ?: WeatherCache.load(context) ?: WeatherCache.unavailable()
        val city = cityOverride?.trim()?.takeIf { it.isNotBlank() }
            ?: WeatherLocationStore.displayCity(context, fallback = base.location)
        return base.copy(location = city)
    }

    private fun build(
        context: Context,
        weather: WeatherModel,
        config: WidgetCustomizationConfig,
        interactive: Boolean,
    ): RemoteViews {
        val parts = WeatherTemperature.formatParts(context, weather.temperatureCelsius)
        val condition = weather.condition.trim().ifBlank { EMPTY_CONDITION }
        Log.d(TAG, "location=${weather.location} temp=${parts.value}${parts.unit} condition=$condition")

        val temperature = config.element(WeatherCustomization.ELEMENT_TEMPERATURE)
        val unit = config.element(WeatherCustomization.ELEMENT_UNIT)
        val conditionStyle = config.element(WeatherCustomization.ELEMENT_CONDITION)
        val location = config.element(WeatherCustomization.ELEMENT_LOCATION)
        val icon = config.element(WeatherCustomization.ELEMENT_ICON)

        val views = RemoteViews(context.packageName, R.layout.widget_weather)
        views.setImageViewResource(R.id.weather_icon, weather.iconResId)
        views.setBackgroundRes(R.id.widget_weather_root, backgroundRes(config.backgroundColor))
        applyIconStyle(views, icon)

        views.showFontVariant(temperature.fontFamily, TEMPERATURE_VARIANTS, StylePresets.FONT_DEFAULT)
        views.showFontVariant(unit.fontFamily, UNIT_VARIANTS, StylePresets.FONT_DEFAULT)
        views.showFontVariant(conditionStyle.fontFamily, CONDITION_VARIANTS, StylePresets.FONT_DEFAULT)
        views.showFontVariant(location.fontFamily, LOCATION_VARIANTS, StylePresets.FONT_DEFAULT)

        applyText(views, context, TEMPERATURE_VARIANTS, parts.value, temperature)
        applyText(views, context, UNIT_VARIANTS, parts.unit, unit)
        applyText(views, context, CONDITION_VARIANTS, condition, conditionStyle)
        applyText(views, context, LOCATION_VARIANTS, weather.location, location)

        if (interactive) {
            val clickIntent = WeatherClickHelper.pendingIntent(context)
            CLICK_TARGETS.forEach { id -> views.setOnClickPendingIntent(id, clickIntent) }
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

    private fun applyIconStyle(views: RemoteViews, icon: ElementStyleConfig) {
        when (icon.iconStyle) {
            StylePresets.ICON_MONO -> views.tintImage(R.id.weather_icon, icon.textColor)
            else -> views.tintImage(R.id.weather_icon, NO_TINT)
        }
    }

    private fun backgroundRes(@ColorInt color: Int): Int = when (color) {
        WeatherCustomization.COLOR_BLACK -> R.drawable.weather_background_black
        WeatherCustomization.COLOR_LIGHT -> R.drawable.weather_background_light
        else -> R.drawable.weather_background
    }

    private fun sp(context: Context, sizeSp: Int): Float =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sizeSp.toFloat(),
            context.resources.displayMetrics,
        )
}
