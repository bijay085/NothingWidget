package com.phoenix.nothingwidget.widgets.screen_time

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

/**
 * Keeps Screen Time widgets fresh when AlarmManager is deferred by Doze / OEM battery.
 * Periodic (15m) plus a short one-shot chain while at least one widget is pinned.
 */
class ScreenTimeRefreshWorker(
    appContext: Context,
    params: WorkerParameters,
) : Worker(appContext, params) {

    override fun doWork(): Result {
        return try {
            if (!hasPinnedWidgets(applicationContext)) {
                cancel(applicationContext)
                return Result.success()
            }
            ScreenTimeWidgetRenderer.renderAll(applicationContext)
            enqueueNext(applicationContext)
            Result.success()
        } catch (error: Exception) {
            Log.e(TAG, "Screen Time worker failed", error)
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "ScreenTimeWorker"
        private const val UNIQUE_PERIODIC = "screen_time_widget_periodic_refresh"
        private const val UNIQUE_CHAIN = "screen_time_widget_refresh_chain"

        fun enqueuePeriodic(context: Context) {
            if (!hasPinnedWidgets(context)) return
            val request = PeriodicWorkRequestBuilder<ScreenTimeRefreshWorker>(
                15,
                TimeUnit.MINUTES,
            ).build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE_PERIODIC,
                ExistingPeriodicWorkPolicy.KEEP,
                request,
            )
        }

        fun enqueueNow(context: Context) {
            if (!hasPinnedWidgets(context)) return
            val request = OneTimeWorkRequestBuilder<ScreenTimeRefreshWorker>().build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                UNIQUE_CHAIN,
                ExistingWorkPolicy.REPLACE,
                request,
            )
        }

        fun enqueueNext(context: Context) {
            if (!hasPinnedWidgets(context)) {
                cancel(context)
                return
            }
            val request = OneTimeWorkRequestBuilder<ScreenTimeRefreshWorker>()
                .setInitialDelay(ScreenTimeConfig.REFRESH_INTERVAL_MS, TimeUnit.MILLISECONDS)
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                UNIQUE_CHAIN,
                ExistingWorkPolicy.REPLACE,
                request,
            )
        }

        fun ensureScheduled(context: Context) {
            enqueuePeriodic(context)
            enqueueNext(context)
        }

        fun cancel(context: Context) {
            val wm = WorkManager.getInstance(context)
            wm.cancelUniqueWork(UNIQUE_PERIODIC)
            wm.cancelUniqueWork(UNIQUE_CHAIN)
        }

        private fun hasPinnedWidgets(context: Context): Boolean {
            val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(
                ComponentName(context, ScreenTimeWidgetReceiver::class.java),
            )
            return ids.isNotEmpty()
        }
    }
}
