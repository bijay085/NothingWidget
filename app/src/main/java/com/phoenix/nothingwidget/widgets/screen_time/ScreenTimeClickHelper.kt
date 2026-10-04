package com.phoenix.nothingwidget.widgets.screen_time

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings

/**
 * Tap target for Screen Time widget: Digital Wellbeing / Screen Time settings.
 * Without usage access, opens the Usage Access screen so the widget can read data.
 */
object ScreenTimeClickHelper {

    private const val REQUEST_CODE = 5210

    private val SCREEN_TIME_PACKAGES = listOf(
        "com.google.android.apps.wellbeing",
        "com.samsung.android.lool", // Samsung Digital Wellbeing / Device care variants
        "com.samsung.android.forest",
        "com.miui.securitycenter",
        "com.coloros.phonemanager",
        "com.oneplus.security",
        "com.nothing.hearthstone",
    )

    private val WELLBEING_COMPONENTS = listOf(
        ComponentName(
            "com.google.android.apps.wellbeing",
            "com.google.android.apps.wellbeing.settings.TopLevelSettingsActivity",
        ),
        ComponentName(
            "com.google.android.apps.wellbeing",
            "com.google.android.apps.wellbeing.screen.ui.ScreenActivity",
        ),
        ComponentName(
            "com.google.android.apps.wellbeing",
            "com.google.android.apps.wellbeing.screen.ui.DashboardActivity",
        ),
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
        if (!ScreenTimeRepository.hasUsageAccess(context)) {
            return ScreenTimeRepository.usageAccessSettingsIntent()
        }

        val pm = context.packageManager

        // Digital Wellbeing — App usage / Screen Time dashboard
        val dashboard = Intent("com.google.android.apps.wellbeing.action.APP_USAGE_DASHBOARD")
        if (dashboard.resolveActivity(pm) != null) return dashboard

        for (component in WELLBEING_COMPONENTS) {
            val intent = Intent().setComponent(component)
            if (intent.resolveActivity(pm) != null) return intent
        }

        for (packageName in SCREEN_TIME_PACKAGES) {
            if (!isInstalled(pm, packageName)) continue
            val launch = pm.getLaunchIntentForPackage(packageName) ?: continue
            return launch
        }

        // Last resort: usage access / special app access in system Settings.
        return Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    private fun isInstalled(pm: PackageManager, packageName: String): Boolean {
        return try {
            pm.getPackageInfo(packageName, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }
}
