package com.phoenix.nothingwidget.widgets.weather

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.util.Log

/**
 * Weather AppWidgetProvider: no UI styling here.
 * All RemoteViews updates go through [WeatherWidgetRenderer].
 */
class WeatherWidgetReceiver : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        try {
            WeatherWidgetRenderer.render(context, appWidgetManager, appWidgetIds)
        } catch (error: Exception) {
            Log.e(TAG, "Weather widget update failed", error)
        }
        WeatherRefreshWorker.enqueuePeriodic(context)
        WeatherRefreshWorker.enqueueNow(context)
    }

    override fun onEnabled(context: Context) {
        captureLocationThenRefresh(context)
    }

    override fun onDisabled(context: Context) {
        WeatherRefreshWorker.cancel(context)
    }

    companion object {
        private const val TAG = "WeatherWidget"

        fun pushCurrent(context: Context) {
            WeatherWidgetRenderer.renderAll(context)
        }

        fun requestRefresh(context: Context) {
            WeatherWidgetRenderer.renderAll(context)
            WeatherRefreshWorker.enqueueNow(context.applicationContext)
        }

        fun captureLocationThenRefresh(context: Context) {
            val appContext = context.applicationContext
            WeatherLocationHelper.captureAndPersist(appContext) { city ->
                WeatherWidgetRenderer.renderAll(appContext, cityOverride = city)
                WeatherRefreshWorker.enqueueNow(appContext)
            }
        }
    }
}
