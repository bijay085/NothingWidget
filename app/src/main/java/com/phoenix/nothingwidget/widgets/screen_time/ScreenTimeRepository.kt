package com.phoenix.nothingwidget.widgets.screen_time

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Process
import android.provider.Settings
import android.util.Log
import java.util.Calendar
import java.util.Locale

/**
 * Reads today's screen time from [UsageStatsManager].
 *
 * Total: wall-clock foreground time via UsageEvents (midnight → now, device TZ),
 * counting only one foreground app at a time and skipping launcher / System UI.
 * A raw INTERVAL_DAILY sum of every package (~10h) does not match Digital Wellbeing.
 *
 * Top app: INTERVAL_DAILY per-package totals + launchable filter (unchanged).
 */
object ScreenTimeRepository {

    private const val TAG = "ScreenTimeRepo"

    fun hasUsageAccess(context: Context): Boolean {
        val appOps = context.getSystemService(AppOpsManager::class.java) ?: return false
        val mode = appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName,
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun usageAccessSettingsIntent(): Intent =
        Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    fun loadToday(context: Context): ScreenTimeModel {
        if (!hasUsageAccess(context)) {
            return ScreenTimeModel.unavailable()
        }

        val usageStatsManager = context.getSystemService(UsageStatsManager::class.java)
            ?: return ScreenTimeModel.empty()

        // Device timezone midnight (Calendar default zone = device zone). No UTC.
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val beginTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        Log.d(TAG, "SCREEN_TIME_START: $beginTime")
        Log.d(TAG, "SCREEN_TIME_END: $endTime")

        if (endTime <= beginTime) {
            return ScreenTimeModel.empty()
        }

        val totalMs = totalForegroundToday(usageStatsManager, beginTime, endTime)
        Log.d(TAG, "TOTAL_MINUTES: ${totalMs / 60_000L}")

        val byPackage = dailyPackageForeground(usageStatsManager, beginTime, endTime)
        if (totalMs <= 0L && byPackage.isEmpty()) {
            return ScreenTimeModel.empty()
        }

        // Prefer launchable apps; fill remaining slots from other non-system packages.
        val topAppsLimit = 12
        val rankedLaunchable = byPackage
            .filterKeys { packageName -> isLaunchableApp(context, packageName) }
            .filterValues { ms -> ms >= 60_000L }
            .entries
            .sortedByDescending { it.value }

        val rankedAny = byPackage
            .filterKeys { packageName -> !isExcludedFromTotal(packageName) }
            .filterValues { ms -> ms >= 60_000L }
            .entries
            .sortedByDescending { it.value }

        // Small widget top app: same filter as before (excludes this app).
        val topEntry = rankedLaunchable.firstOrNull { isCandidateTopApp(context, it.key) }
            ?: byPackage.maxByOrNull { it.value }

        val topPackage = topEntry?.key
        val topMs = topEntry?.value ?: 0L
        val topName = topPackage?.let { resolveAppLabel(context, it) } ?: "No usage yet"

        val topApps = linkedMapOf<String, Long>()
        for (entry in rankedLaunchable) {
            if (topApps.size >= topAppsLimit) break
            topApps[entry.key] = entry.value
        }
        for (entry in rankedAny) {
            if (topApps.size >= topAppsLimit) break
            if (entry.key !in topApps) topApps[entry.key] = entry.value
        }

        val topAppItems = topApps.entries
            .sortedByDescending { it.value }
            .take(topAppsLimit)
            .map { (pkg, ms) ->
                TopAppUsageItem(
                    appName = resolveAppLabel(context, pkg),
                    usageTime = ms,
                    packageName = pkg,
                )
            }

        Log.d(
            TAG,
            "TOP_APPS: ${topAppItems.joinToString { "${it.appName}=${it.usageTime / 60_000L}m" }}",
        )

        return ScreenTimeModel(
            totalScreenTimeToday = totalMs,
            topAppName = topName,
            topAppUsageTime = topMs,
            hasPermission = true,
            topApps = topAppItems,
        )
    }

    /**
     * Digital Wellbeing-style total: walk UsageEvents, keep a single current foreground
     * session, ignore launcher / System UI sessions.
     */
    private fun totalForegroundToday(
        usageStatsManager: UsageStatsManager,
        beginTime: Long,
        endTime: Long,
    ): Long {
        val events = usageStatsManager.queryEvents(beginTime, endTime)
        val event = UsageEvents.Event()
        var currentPkg: String? = null
        var currentStart = 0L
        var counting = false
        var total = 0L

        fun closeAt(time: Long) {
            if (counting && currentPkg != null && time > currentStart) {
                total += time - currentStart
            }
            currentPkg = null
            counting = false
        }

        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            val pkg = event.packageName ?: continue
            when (event.eventType) {
                UsageEvents.Event.ACTIVITY_RESUMED,
                UsageEvents.Event.MOVE_TO_FOREGROUND,
                -> {
                    if (pkg == currentPkg) continue
                    closeAt(event.timeStamp)
                    currentPkg = pkg
                    currentStart = event.timeStamp
                    counting = !isExcludedFromTotal(pkg)
                }

                UsageEvents.Event.ACTIVITY_PAUSED,
                UsageEvents.Event.MOVE_TO_BACKGROUND,
                -> {
                    if (pkg == currentPkg) closeAt(event.timeStamp)
                }
            }
        }
        closeAt(endTime)
        return total
    }

    /** Per-package daily foreground totals for top-app selection only. */
    private fun dailyPackageForeground(
        usageStatsManager: UsageStatsManager,
        beginTime: Long,
        endTime: Long,
    ): Map<String, Long> {
        val statsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            beginTime,
            endTime,
        ).orEmpty()

        val byPackage = LinkedHashMap<String, Long>()
        for (stat: UsageStats in statsList) {
            if (stat.totalTimeInForeground <= 0L) continue
            if (stat.lastTimeUsed < beginTime) continue
            val pkg = stat.packageName
            val existing = byPackage[pkg] ?: 0L
            if (stat.totalTimeInForeground > existing) {
                byPackage[pkg] = stat.totalTimeInForeground
            }
        }
        return byPackage
    }

    private fun isExcludedFromTotal(packageName: String): Boolean {
        if (packageName == "android") return true
        if (packageName.contains("systemui", ignoreCase = true)) return true
        if (packageName.contains("launcher", ignoreCase = true)) return true
        if (packageName.contains("permissioncontroller", ignoreCase = true)) return true
        return false
    }

    private fun isCandidateTopApp(context: Context, packageName: String): Boolean {
        if (packageName == context.packageName) return false
        return isLaunchableApp(context, packageName)
    }

    /** Launchable apps for ranking (includes this app; excludes launcher / System UI). */
    private fun isLaunchableApp(context: Context, packageName: String): Boolean {
        if (isExcludedFromTotal(packageName)) return false
        return try {
            context.packageManager.getLaunchIntentForPackage(packageName) != null
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun resolveAppLabel(context: Context, packageName: String): String {
        return try {
            val pm = context.packageManager
            val info = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(info).toString()
        } catch (_: PackageManager.NameNotFoundException) {
            packageName.substringAfterLast('.')
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }
    }
}

