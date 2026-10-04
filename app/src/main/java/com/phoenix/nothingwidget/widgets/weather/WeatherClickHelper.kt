package com.phoenix.nothingwidget.widgets.weather

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build

/**
 * Resolves a tap target for the Weather widget: installed weather app, then browser.
 * Never opens this app's MainActivity.
 * Always returns a PendingIntent so the widget stays clickable.
 */
object WeatherClickHelper {

    private const val REQUEST_CODE = 4201
    private val BROWSER_FALLBACK = Uri.parse("https://weather.google.com/")

    private val WEATHER_PACKAGES = listOf(
        "com.google.android.apps.weather",
        "com.sec.android.daemonapp",
        "com.huawei.android.totemweather",
        "com.miui.weather2",
        "net.oneplus.weather",
        "com.nothing.weather",
        "com.accuweather.android",
        "com.weather.Weather",
        "com.yahoo.mobile.client.android.weather",
    )

    fun pendingIntent(context: Context): PendingIntent {
        val intent = resolve(context).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return PendingIntent.getActivity(
            context.applicationContext,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun resolve(context: Context): Intent {
        val packageManager = context.packageManager
        val ourPackage = context.packageName

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val weatherCategory = Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_APP_WEATHER)
            val resolved = weatherCategory.resolveActivity(packageManager)
            if (resolved != null && resolved.packageName != ourPackage) {
                return weatherCategory
            }
        }

        for (packageName in WEATHER_PACKAGES) {
            if (!isPackageInstalled(packageManager, packageName)) continue
            val launch = packageManager.getLaunchIntentForPackage(packageName) ?: continue
            if (launch.component?.packageName == ourPackage) continue
            return launch
        }

        return Intent(Intent.ACTION_VIEW, BROWSER_FALLBACK).addCategory(Intent.CATEGORY_BROWSABLE)
    }

    private fun isPackageInstalled(packageManager: PackageManager, packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }
}
