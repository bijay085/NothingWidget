package com.phoenix.nothingwidget.widgets.round_clock

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import com.phoenix.nothingwidget.R
import com.phoenix.nothingwidget.core.widget_config.ElementStyleConfig
import com.phoenix.nothingwidget.core.widget_config.StylePresets
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomizationConfig
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomizationRepository
import com.phoenix.nothingwidget.core.widget_config.isTransparent
import com.phoenix.nothingwidget.core.widget_config.setSolidBackground
import com.phoenix.nothingwidget.core.widget_config.setTextColors
import com.phoenix.nothingwidget.core.widget_config.setTextSizePx
import com.phoenix.nothingwidget.core.widget_config.showFontVariant
import com.phoenix.nothingwidget.core.widget_config.tintImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Builds Round Clock RemoteViews from [WidgetCustomizationConfig].
 * Every styled property is set on each render so launcher re-applies stay correct.
 */
object RoundClockRenderer {

    private const val NO_TINT = 0x00000000

    private val HOUR_VARIANTS = mapOf(
        StylePresets.FONT_NDOT to R.id.round_clock_hour,
        StylePresets.FONT_SANS to R.id.round_clock_hour_sans,
        StylePresets.FONT_CLEAN to R.id.round_clock_hour_sans,
        StylePresets.FONT_DIGITAL to R.id.round_clock_hour_digital,
        StylePresets.FONT_FUTURISTIC to R.id.round_clock_hour_futuristic,
        StylePresets.FONT_PIXEL_MONO to R.id.round_clock_hour_pixel,
    )
    private val MINUTE_VARIANTS = mapOf(
        StylePresets.FONT_NDOT to R.id.round_clock_minute,
        StylePresets.FONT_SANS to R.id.round_clock_minute_sans,
        StylePresets.FONT_CLEAN to R.id.round_clock_minute_sans,
        StylePresets.FONT_DIGITAL to R.id.round_clock_minute_digital,
        StylePresets.FONT_FUTURISTIC to R.id.round_clock_minute_futuristic,
        StylePresets.FONT_PIXEL_MONO to R.id.round_clock_minute_pixel,
    )
    private val DAY_VARIANTS = mapOf(
        StylePresets.FONT_SANS to R.id.round_clock_day,
        StylePresets.FONT_NDOT to R.id.round_clock_day_ndot,
        StylePresets.FONT_DIGITAL to R.id.round_clock_day_digital,
        StylePresets.FONT_CLEAN to R.id.round_clock_day_clean,
        StylePresets.FONT_DARK_SPOOKY to R.id.round_clock_day_spooky,
    )
    private val DATE_VARIANTS = mapOf(
        StylePresets.FONT_SANS to R.id.round_clock_date,
        StylePresets.FONT_NDOT to R.id.round_clock_date_ndot,
        StylePresets.FONT_DIGITAL to R.id.round_clock_date_digital,
        StylePresets.FONT_CLEAN to R.id.round_clock_date_clean,
        StylePresets.FONT_DARK_SPOOKY to R.id.round_clock_date_spooky,
    )
    private val ALARM_VARIANTS = mapOf(
        StylePresets.FONT_SANS to R.id.round_clock_alarm,
        StylePresets.FONT_CLEAN to R.id.round_clock_alarm,
        StylePresets.FONT_DIGITAL to R.id.round_clock_alarm_digital,
        StylePresets.FONT_DARK_SPOOKY to R.id.round_clock_alarm_spooky,
    )

    private val TIME_VIEWS = (HOUR_VARIANTS.values + MINUTE_VARIANTS.values).toSet().toIntArray()
    private val DATE_VIEWS = (DAY_VARIANTS.values + DATE_VARIANTS.values).toSet().toIntArray()
    private val ALARM_VIEWS = ALARM_VARIANTS.values.toSet().toIntArray()

    fun render(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        if (appWidgetIds.isEmpty()) return
        val config = WidgetCustomizationRepository.config(context, RoundClockCustomization)
        appWidgetManager.updateAppWidget(appWidgetIds, build(context, config, interactive = true))
    }

    fun renderAll(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context, RoundClockWidgetReceiver::class.java))
        render(context, manager, ids)
    }

    fun build(context: Context, config: WidgetCustomizationConfig, interactive: Boolean): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_round_clock)
        val time = config.element(RoundClockCustomization.ELEMENT_TIME)
        val date = config.element(RoundClockCustomization.ELEMENT_DATE)
        val alarm = config.element(RoundClockCustomization.ELEMENT_ALARM)
        val glow = config.element(RoundClockCustomization.ELEMENT_GLOW)

        HOUR_VARIANTS.values.toSet().forEach { id ->
            views.setClockFormat(id, RoundClockConfig.FORMAT_12_HOUR, RoundClockConfig.FORMAT_24_HOUR)
        }
        MINUTE_VARIANTS.values.toSet().forEach { id ->
            views.setClockFormat(id, RoundClockConfig.FORMAT_MINUTE, RoundClockConfig.FORMAT_MINUTE)
        }
        DAY_VARIANTS.values.toSet().forEach { id -> views.setClockFormat(id, "EEE", "EEE") }
        DATE_VARIANTS.values.toSet().forEach { id -> views.setClockFormat(id, "dd", "dd") }

        views.showFontVariant(time.fontFamily, HOUR_VARIANTS, StylePresets.FONT_NDOT)
        views.showFontVariant(time.fontFamily, MINUTE_VARIANTS, StylePresets.FONT_NDOT)
        views.showFontVariant(date.fontFamily, DAY_VARIANTS, StylePresets.FONT_SANS)
        views.showFontVariant(date.fontFamily, DATE_VARIANTS, StylePresets.FONT_SANS)
        views.showFontVariant(alarm.fontFamily, ALARM_VARIANTS, StylePresets.FONT_SANS)

        views.setSolidBackground(
            viewId = R.id.widget_round_clock_root,
            color = config.backgroundColor,
            tintableBaseRes = R.drawable.round_clock_background_tintable,
            transparentRes = R.drawable.round_clock_background_transparent,
            presetResFor = { color ->
                when (color) {
                    RoundClockCustomization.COLOR_BLACK -> R.drawable.round_clock_background_black
                    RoundClockCustomization.COLOR_DARK_NAVY -> R.drawable.round_clock_background
                    else -> null
                }
            },
        )
        views.setTextColors(time.textColor, *TIME_VIEWS)
        views.setTextColor(R.id.round_clock_ampm, time.textColor)
        views.setTextColors(date.textColor, *DATE_VIEWS)
        views.setTextColors(alarm.textColor, *ALARM_VIEWS)

        views.setTextSizePx(sp(context, time.fontSize), *TIME_VIEWS)
        views.setTextSizePx(sp(context, date.fontSize), *DATE_VIEWS)
        views.setTextSizePx(sp(context, alarm.fontSize), *ALARM_VIEWS)

        bindAlarm(context, views, alarm, glow)

        if (interactive) {
            views.setOnClickPendingIntent(R.id.widget_round_clock_root, clockAppPendingIntent(context))
        }
        return views
    }

    private fun bindAlarm(
        context: Context,
        views: RemoteViews,
        alarm: ElementStyleConfig,
        glow: ElementStyleConfig,
    ) {
        val nextAlarm = context.getSystemService(AlarmManager::class.java)?.nextAlarmClock
        val now = System.currentTimeMillis()
        val upcoming = nextAlarm != null && nextAlarm.triggerTime > now
        val alarmText = if (upcoming) {
            formatAlarmTime(nextAlarm!!.triggerTime)
        } else {
            RoundClockConfig.ALARM_PLACEHOLDER
        }

        ALARM_VIEWS.forEach { id -> views.setTextViewText(id, alarmText) }

        if (!glow.enabled || isTransparent(glow.textColor)) {
            views.setViewVisibility(R.id.round_clock_accent, View.INVISIBLE)
            return
        }
        views.setViewVisibility(R.id.round_clock_accent, View.VISIBLE)
        views.tintImage(
            R.id.round_clock_accent,
            if (glow.textColor == StylePresets.GLOW_RED) NO_TINT else glow.textColor,
        )
        views.setInt(
            R.id.round_clock_accent,
            "setImageAlpha",
            if (upcoming) glowAlpha(nextAlarm!!.triggerTime - now) else RoundClockConfig.GLOW_MIN_ALPHA,
        )
    }

    private fun sp(context: Context, sizeSp: Int): Float =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sizeSp.toFloat(),
            context.resources.displayMetrics,
        )

    private fun RemoteViews.setClockFormat(viewId: Int, format12: String, format24: String) {
        setCharSequence(viewId, "setFormat12Hour", format12)
        setCharSequence(viewId, "setFormat24Hour", format24)
    }

    private fun formatAlarmTime(triggerTimeMs: Long): String {
        val formatter = SimpleDateFormat(RoundClockConfig.ALARM_TIME_FORMAT, Locale.getDefault())
        return formatter.format(Date(triggerTimeMs)).uppercase(Locale.getDefault())
    }

    private fun glowAlpha(remainingMs: Long): Int {
        val min = RoundClockConfig.GLOW_FULL_REMAINING_MS
        val max = RoundClockConfig.GLOW_DIM_REMAINING_MS
        val minNorm = RoundClockConfig.GLOW_MIN_ALPHA / RoundClockConfig.GLOW_MAX_ALPHA.toFloat()
        val intensity = when {
            remainingMs <= min -> 1f
            remainingMs >= max -> minNorm
            else -> {
                val ratio = (remainingMs - min).toFloat() / (max - min).toFloat()
                1f - ratio * (1f - minNorm)
            }
        }
        return (intensity * RoundClockConfig.GLOW_MAX_ALPHA)
            .toInt()
            .coerceIn(RoundClockConfig.GLOW_MIN_ALPHA, RoundClockConfig.GLOW_MAX_ALPHA)
    }

    private fun clockAppPendingIntent(context: Context): PendingIntent {
        val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
