package com.phoenix.nothingwidget.widgets.screen_time_large

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log

/**
 * Resizable Screen Time Large AppWidgetProvider.
 * Shares [com.phoenix.nothingwidget.widgets.screen_time.ScreenTimeRepository] with the small widget.
 * Short height → 2 apps; taller → up to 6.
 */
class ScreenTimeLargeWidgetReceiver : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        when (action) {
            ScreenTimeLargeConfig.ACTION_REFRESH,
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_DATE_CHANGED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            -> {
                val manager = AppWidgetManager.getInstance(context)
                val ids = manager.getAppWidgetIds(
                    ComponentName(context, ScreenTimeLargeWidgetReceiver::class.java),
                )
                if (ids.isNotEmpty()) {
                    onUpdate(context, manager, ids)
                } else if (action == ScreenTimeLargeConfig.ACTION_REFRESH) {
                    cancelRefresh(context)
                }
            }

            else -> super.onReceive(context, intent)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        try {
            ScreenTimeLargeRenderer.render(context, appWidgetManager, appWidgetIds)
        } catch (error: Exception) {
            Log.e(TAG, "Screen Time Large update failed", error)
        }
        scheduleRefresh(context)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle,
    ) {
        try {
            ScreenTimeLargeRenderer.render(context, appWidgetManager, intArrayOf(appWidgetId))
        } catch (error: Exception) {
            Log.e(TAG, "Screen Time Large resize update failed", error)
        }
    }

    override fun onEnabled(context: Context) {
        scheduleRefresh(context)
    }

    override fun onDisabled(context: Context) {
        cancelRefresh(context)
    }

    private fun scheduleRefresh(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + ScreenTimeLargeConfig.REFRESH_INTERVAL_MS,
            refreshPendingIntent(context),
        )
    }

    private fun cancelRefresh(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        alarmManager.cancel(refreshPendingIntent(context))
    }

    private fun refreshPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, ScreenTimeLargeWidgetReceiver::class.java).apply {
            action = ScreenTimeLargeConfig.ACTION_REFRESH
        }
        return PendingIntent.getBroadcast(
            context,
            REFRESH_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        private const val TAG = "ScreenTimeLarge"
        private const val REFRESH_REQUEST_CODE = 5300

        fun requestRefresh(context: Context) {
            val app = context.applicationContext
            app.sendBroadcast(
                Intent(app, ScreenTimeLargeWidgetReceiver::class.java)
                    .setAction(ScreenTimeLargeConfig.ACTION_REFRESH),
            )
        }
    }
}
