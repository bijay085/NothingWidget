package com.phoenix.nothingwidget.widgets.round_clock

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent

/**
 * App widget provider for the Round Clock widget. Rendering lives in [RoundClockRenderer].
 */
class RoundClockWidgetReceiver : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            RoundClockConfig.ACTION_REFRESH,
            AlarmManager.ACTION_NEXT_ALARM_CLOCK_CHANGED,
            -> {
                val manager = AppWidgetManager.getInstance(context)
                val ids = manager.getAppWidgetIds(
                    ComponentName(context, RoundClockWidgetReceiver::class.java),
                )
                if (ids.isNotEmpty()) {
                    onUpdate(context, manager, ids)
                }
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        RoundClockRenderer.render(context, appWidgetManager, appWidgetIds)
        scheduleRefresh(context)
    }

    override fun onEnabled(context: Context) {
        scheduleRefresh(context)
    }

    override fun onDisabled(context: Context) {
        cancelRefresh(context)
    }

    private fun scheduleRefresh(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        val triggerAt = System.currentTimeMillis() + RoundClockConfig.REFRESH_INTERVAL_MS
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC,
            triggerAt,
            refreshPendingIntent(context),
        )
    }

    private fun cancelRefresh(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        alarmManager.cancel(refreshPendingIntent(context))
    }

    private fun refreshPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, RoundClockWidgetReceiver::class.java).apply {
            action = RoundClockConfig.ACTION_REFRESH
        }
        return PendingIntent.getBroadcast(
            context,
            REFRESH_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        private const val REFRESH_REQUEST_CODE = 4101
    }
}
