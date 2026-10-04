package com.phoenix.nothingwidget.widgets.weather

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class WeatherRefreshWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val weather = WeatherRepository.fetchWeather(applicationContext)
            WeatherWidgetRenderer.renderAll(
                context = applicationContext,
                weatherOverride = weather,
            )
            Result.success()
        } catch (error: Exception) {
            Log.e(TAG, "Weather worker failed", error)
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "WeatherWorker"
        private const val UNIQUE_PERIODIC = "weather_widget_periodic_refresh"
        private const val UNIQUE_ONCE = "weather_widget_one_shot_refresh"

        fun enqueuePeriodic(context: Context) {
            val request = PeriodicWorkRequestBuilder<WeatherRefreshWorker>(
                WeatherConfig.REFRESH_INTERVAL_MINUTES,
                TimeUnit.MINUTES,
            ).build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE_PERIODIC,
                ExistingPeriodicWorkPolicy.KEEP,
                request,
            )
        }

        fun enqueueNow(context: Context) {
            val request = OneTimeWorkRequestBuilder<WeatherRefreshWorker>().build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                UNIQUE_ONCE,
                ExistingWorkPolicy.REPLACE,
                request,
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_PERIODIC)
        }
    }
}
