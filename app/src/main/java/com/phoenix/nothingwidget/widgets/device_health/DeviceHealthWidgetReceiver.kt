package com.phoenix.nothingwidget.widgets.device_health

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log

class DeviceHealthWidgetReceiver : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            DeviceHealthConfig.ACTION_SET_PAGE -> {
                val id = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID,
                )
                if (id != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    DeviceHealthPageStore.setPage(
                        context,
                        id,
                        intent.getIntExtra(
                            DeviceHealthConfig.EXTRA_PAGE,
                            DeviceHealthConfig.PAGE_BATTERY,
                        ),
                    )
                    DeviceHealthRenderer.render(
                        context,
                        AppWidgetManager.getInstance(context),
                        intArrayOf(id),
                    )
                }
            }

            DeviceHealthConfig.ACTION_REFRESH,
            Intent.ACTION_POWER_CONNECTED,
            Intent.ACTION_POWER_DISCONNECTED,
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            -> {
                val manager = AppWidgetManager.getInstance(context)
                val ids = manager.getAppWidgetIds(
                    ComponentName(context, DeviceHealthWidgetReceiver::class.java),
                )
                if (ids.isNotEmpty()) {
                    onUpdate(context, manager, ids)
                } else if (intent.action == DeviceHealthConfig.ACTION_REFRESH) {
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
            DeviceHealthRenderer.render(context, appWidgetManager, appWidgetIds)
        } catch (error: Exception) {
            Log.e(TAG, "Device Health update failed", error)
        }
        scheduleRefresh(context)
    }

    override fun onEnabled(context: Context) = scheduleRefresh(context)

    override fun onDisabled(context: Context) = cancelRefresh(context)

    private fun scheduleRefresh(context: Context) {
        val am = context.getSystemService(AlarmManager::class.java) ?: return
        val pi = refreshPendingIntent(context)
        am.cancel(pi)
        am.setAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + DeviceHealthConfig.REFRESH_INTERVAL_MS,
            pi,
        )
    }

    private fun cancelRefresh(context: Context) {
        context.getSystemService(AlarmManager::class.java)
            ?.cancel(refreshPendingIntent(context))
    }

    private fun refreshPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, DeviceHealthWidgetReceiver::class.java).apply {
            action = DeviceHealthConfig.ACTION_REFRESH
        }
        return PendingIntent.getBroadcast(
            context.applicationContext,
            REQUEST_REFRESH,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        private const val TAG = "DeviceHealthWidget"
        private const val REQUEST_REFRESH = 7201
    }
}
