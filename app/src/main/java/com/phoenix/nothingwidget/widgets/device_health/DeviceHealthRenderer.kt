package com.phoenix.nothingwidget.widgets.device_health

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.phoenix.nothingwidget.R
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomizationConfig
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomizationRepository
import com.phoenix.nothingwidget.core.widget_config.setSolidBackground

/**
 * ViewFlipper host: 4 pages switched only by ◀ ▶ / dots.
 * No swipe, StackView, or RemoteViewsService.
 */
object DeviceHealthRenderer {

    private val DOT_IDS = intArrayOf(
        R.id.device_health_dot_0,
        R.id.device_health_dot_1,
        R.id.device_health_dot_2,
        R.id.device_health_dot_3,
    )

    fun renderAll(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(
            ComponentName(context, DeviceHealthWidgetReceiver::class.java),
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
        val config = WidgetCustomizationRepository.config(context, DeviceHealthCustomization)
        val model = DeviceHealthModel(
            battery = BatteryHealthRepository.read(context),
            performance = PerformanceRepository.read(context),
        )
        appWidgetIds.forEach { id ->
            appWidgetManager.updateAppWidget(id, build(context, id, model, config))
        }
    }

    fun preview(context: Context, config: WidgetCustomizationConfig): RemoteViews {
        val battery = BatteryHealthRepository.read(context)
        val views = RemoteViews(context.packageName, R.layout.widget_device_health_preview)
        views.setSolidBackground(
            viewId = R.id.widget_device_health_preview_root,
            color = config.backgroundColor,
            tintableBaseRes = R.drawable.device_health_background_tintable,
            presetResFor = { color ->
                if (color == DeviceHealthCustomization.COLOR_BACKGROUND) {
                    R.drawable.device_health_background
                } else {
                    null
                }
            },
        )
        val percentStyle = config.element(DeviceHealthConfig.ELEMENT_BATTERY_PERCENT)
        val chargingStyle = config.element(DeviceHealthConfig.ELEMENT_BATTERY_CHARGING)
        val percentText = if (battery.percentage < 0) DEVICE_HEALTH_NA else "${battery.percentage}%"
        views.setTextViewText(R.id.device_health_preview_percent, percentText)
        views.setTextColor(R.id.device_health_preview_percent, percentStyle.textColor)
        views.setTextViewText(R.id.device_health_preview_status, battery.statusSimple)
        views.setTextColor(R.id.device_health_preview_status, chargingStyle.textColor)
        views.setTextViewText(R.id.device_health_preview_detail, battery.temperature)
        return views
    }

    private fun build(
        context: Context,
        appWidgetId: Int,
        model: DeviceHealthModel,
        config: WidgetCustomizationConfig,
    ): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_device_health)
        views.setSolidBackground(
            viewId = R.id.widget_device_health_root,
            color = config.backgroundColor,
            tintableBaseRes = R.drawable.device_health_background_tintable,
            presetResFor = { color ->
                if (color == DeviceHealthCustomization.COLOR_BACKGROUND) {
                    R.drawable.device_health_background
                } else {
                    null
                }
            },
        )

        bindOverview(views, model.battery, config)
        bindCharging(views, model.battery)
        bindDetails(views, model.battery)
        bindPerformance(views, model.performance, config)

        val page = DeviceHealthPageStore.getPage(context, appWidgetId)
        views.setDisplayedChild(R.id.device_health_flipper, page)
        applyIndicator(views, page)

        val absorb = absorbClickPi(context, appWidgetId)
        views.setOnClickPendingIntent(R.id.widget_device_health_root, absorb)
        views.setOnClickPendingIntent(R.id.device_health_flipper, absorb)
        views.setOnClickPendingIntent(R.id.device_health_overview_root, absorb)
        views.setOnClickPendingIntent(R.id.device_health_charging_root, absorb)
        views.setOnClickPendingIntent(R.id.device_health_details_root, absorb)
        views.setOnClickPendingIntent(R.id.device_health_perf_root, absorb)

        val prev = DeviceHealthPageStore.adjacent(page, -1)
        val next = DeviceHealthPageStore.adjacent(page, 1)
        views.setOnClickPendingIntent(R.id.device_health_btn_left, pagePi(context, appWidgetId, prev))
        views.setOnClickPendingIntent(R.id.device_health_btn_right, pagePi(context, appWidgetId, next))
        DOT_IDS.forEachIndexed { index, id ->
            views.setOnClickPendingIntent(id, pagePi(context, appWidgetId, index))
        }
        return views
    }

    private fun bindOverview(
        views: RemoteViews,
        battery: BatteryHealth,
        config: WidgetCustomizationConfig,
    ) {
        val percentStyle = config.element(DeviceHealthConfig.ELEMENT_BATTERY_PERCENT)
        val chargingStyle = config.element(DeviceHealthConfig.ELEMENT_BATTERY_CHARGING)
        val percentText = if (battery.percentage < 0) DEVICE_HEALTH_NA else "${battery.percentage}%"

        views.setTextViewText(R.id.device_health_overview_percent, percentText)
        views.setTextColor(R.id.device_health_overview_percent, percentStyle.textColor)
        views.setTextViewText(R.id.device_health_overview_status, battery.statusSimple)
        views.setTextColor(R.id.device_health_overview_status, chargingStyle.textColor)
        views.setTextViewText(R.id.device_health_overview_temp, battery.temperature)
        views.setTextViewText(R.id.device_health_overview_health, battery.health)
        views.setTextViewText(R.id.device_health_overview_cycle, battery.cycleCount)
    }

    private fun bindCharging(views: RemoteViews, battery: BatteryHealth) {
        views.setTextViewText(R.id.device_health_charging_status, battery.statusDetail)
        views.setTextViewText(R.id.device_health_charging_power, battery.power)
        views.setTextViewText(R.id.device_health_charging_voltage, battery.voltage)
        views.setTextViewText(R.id.device_health_charging_current, battery.current)
        views.setTextViewText(R.id.device_health_charging_full_in, battery.timeToFull)
    }

    private fun bindDetails(views: RemoteViews, battery: BatteryHealth) {
        views.setTextViewText(R.id.device_health_details_health, battery.health)
        views.setTextViewText(R.id.device_health_details_capacity, battery.capacityMah)
        views.setTextViewText(R.id.device_health_details_design, battery.designCapacityMah)
        views.setTextViewText(R.id.device_health_details_cycles, battery.cycleCount)
        views.setTextViewText(R.id.device_health_details_tech, battery.technology)
    }

    private fun bindPerformance(
        views: RemoteViews,
        performance: DevicePerformance,
        config: WidgetCustomizationConfig,
    ) {
        val ram = config.element(DeviceHealthConfig.ELEMENT_PERF_RAM)
        val cpu = config.element(DeviceHealthConfig.ELEMENT_PERF_CPU)
        val temp = config.element(DeviceHealthConfig.ELEMENT_PERF_TEMP)

        views.setTextViewText(R.id.device_health_perf_ram_value, performance.ramLine)
        views.setTextColor(R.id.device_health_perf_ram_value, ram.textColor)
        views.setTextViewText(R.id.device_health_perf_cpu_value, performance.cpuUsage)
        views.setTextColor(R.id.device_health_perf_cpu_value, cpu.textColor)
        views.setTextViewText(R.id.device_health_perf_storage_value, performance.storageLine)
        views.setTextViewText(R.id.device_health_perf_temp_value, performance.deviceTemperature)
        views.setTextColor(R.id.device_health_perf_temp_value, temp.textColor)
    }

    private fun applyIndicator(views: RemoteViews, page: Int) {
        DOT_IDS.forEachIndexed { index, id ->
            views.setImageViewResource(
                id,
                if (index == page) R.drawable.device_health_dot_on else R.drawable.device_health_dot_off,
            )
        }
    }

    private fun pagePi(context: Context, appWidgetId: Int, page: Int): PendingIntent {
        val intent = Intent(context, DeviceHealthWidgetReceiver::class.java).apply {
            action = DeviceHealthConfig.ACTION_SET_PAGE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            putExtra(DeviceHealthConfig.EXTRA_PAGE, page)
            data = Uri.parse("devicehealth://page/$appWidgetId/$page")
        }
        return PendingIntent.getBroadcast(
            context.applicationContext,
            7300 + appWidgetId * 10 + page,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun absorbClickPi(context: Context, appWidgetId: Int): PendingIntent {
        val intent = Intent(context, DeviceHealthWidgetReceiver::class.java).apply {
            action = DeviceHealthConfig.ACTION_REFRESH
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            data = Uri.parse("devicehealth://absorb/$appWidgetId")
        }
        return PendingIntent.getBroadcast(
            context.applicationContext,
            7200 + appWidgetId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
