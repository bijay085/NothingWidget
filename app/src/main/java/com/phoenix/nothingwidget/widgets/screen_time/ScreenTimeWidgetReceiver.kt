package com.phoenix.nothingwidget.widgets.screen_time

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Screen Time AppWidgetProvider — data from [UsageStatsManager] via [ScreenTimeRepository].
 *
 * Refresh sources:
 * - AlarmManager every [ScreenTimeConfig.REFRESH_INTERVAL_MS]
 * - WorkManager chain (OEM / Doze backup)
 * - System [AppWidgetManager] period (30 min floor)
 * - Boot / package replace / date change
 */
class ScreenTimeWidgetReceiver : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        when (action) {
            ScreenTimeConfig.ACTION_REFRESH,
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_DATE_CHANGED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            -> {
                val manager = AppWidgetManager.getInstance(context)
                val ids = manager.getAppWidgetIds(
                    ComponentName(context, ScreenTimeWidgetReceiver::class.java),
                )
                if (ids.isNotEmpty()) {
                    onUpdate(context, manager, ids)
                } else if (action == ScreenTimeConfig.ACTION_REFRESH) {
                    cancelRefresh(context)
                    ScreenTimeRefreshWorker.cancel(context)
                }
            }

            ScreenTimeConfig.ACTION_OPEN_ACCESS -> {
                try {
                    context.startActivity(ScreenTimeRepository.usageAccessSettingsIntent())
                } catch (error: Exception) {
                    Log.e(TAG, "Could not open usage access settings", error)
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
            ScreenTimeWidgetRenderer.render(context, appWidgetManager, appWidgetIds)
        } catch (error: Exception) {
            Log.e(TAG, "Screen Time widget update failed", error)
        }
        scheduleRefresh(context)
        ScreenTimeRefreshWorker.ensureScheduled(context)
    }

    override fun onEnabled(context: Context) {
        scheduleRefresh(context)
        ScreenTimeRefreshWorker.ensureScheduled(context)
    }

    override fun onDisabled(context: Context) {
        cancelRefresh(context)
        ScreenTimeRefreshWorker.cancel(context)
    }

    private fun scheduleRefresh(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        val triggerAt = System.currentTimeMillis() + ScreenTimeConfig.REFRESH_INTERVAL_MS
        // RTC_WAKEUP so the next tick is not postponed until an unrelated wake.
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAt,
            refreshPendingIntent(context),
        )
    }

    private fun cancelRefresh(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        alarmManager.cancel(refreshPendingIntent(context))
    }

    private fun refreshPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, ScreenTimeWidgetReceiver::class.java).apply {
            action = ScreenTimeConfig.ACTION_REFRESH
        }
        return PendingIntent.getBroadcast(
            context,
            REFRESH_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        private const val TAG = "ScreenTimeWidget"
        private const val REFRESH_REQUEST_CODE = 5200

        fun requestRefresh(context: Context) {
            val app = context.applicationContext
            // Goes through onUpdate so AlarmManager + WorkManager are re-armed.
            app.sendBroadcast(
                Intent(app, ScreenTimeWidgetReceiver::class.java)
                    .setAction(ScreenTimeConfig.ACTION_REFRESH),
            )
        }
    }
}
