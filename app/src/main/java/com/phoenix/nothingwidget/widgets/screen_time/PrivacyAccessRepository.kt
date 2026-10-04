package com.phoenix.nothingwidget.widgets.screen_time

import android.app.AppOpsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Recent camera / microphone / location access via [AppOpsManager].
 *
 * Queries platform ops through [AppOpsManager.getPackagesForOps] (hidden / privileged).
 * Regular apps usually lack [android.Manifest.permission.GET_APP_OPS_STATS]; when the
 * platform denies the query, returns empty entries (UI shows "No recent access").
 * Never invents app names or times.
 */
object PrivacyAccessRepository {

    /** Matches AppOpsManager.OP_FLAGS_ALL_TRUSTED (hidden). */
    private const val OP_FLAGS_ALL_TRUSTED = 0x1 or 0x2 or 0x8

    fun load(context: Context): PrivacyAccessModel {
        val appOps = context.getSystemService(AppOpsManager::class.java)
            ?: return PrivacyAccessModel.empty()

        val packageOpsList = queryPackageOps(appOps) ?: return PrivacyAccessModel.empty()
        if (packageOpsList.isEmpty()) return PrivacyAccessModel.empty()

        var cameraBest: TimedAccess? = null
        var micBest: TimedAccess? = null
        var locationBest: TimedAccess? = null

        for (pkgOps in packageOpsList) {
            val packageName = packageNameOf(pkgOps) ?: continue
            val ops = opsOf(pkgOps) ?: continue
            for (op in ops) {
                val accessTime = lastAccessTimeMs(op)
                if (accessTime <= 0L) continue
                val timed = TimedAccess(packageName, accessTime)
                when (opStrOf(op)) {
                    AppOpsManager.OPSTR_CAMERA -> {
                        if (cameraBest == null || timed.atMs > cameraBest.atMs) cameraBest = timed
                    }
                    AppOpsManager.OPSTR_RECORD_AUDIO -> {
                        if (micBest == null || timed.atMs > micBest.atMs) micBest = timed
                    }
                    AppOpsManager.OPSTR_FINE_LOCATION,
                    AppOpsManager.OPSTR_COARSE_LOCATION,
                    -> {
                        if (locationBest == null || timed.atMs > locationBest.atMs) {
                            locationBest = timed
                        }
                    }
                }
            }
        }

        return PrivacyAccessModel(
            camera = toEntry(context, cameraBest),
            microphone = toEntry(context, micBest),
            location = toEntry(context, locationBest),
        )
    }

    /**
     * Calls hidden [AppOpsManager.getPackagesForOps] via reflection so the project
     * compiles against the public SDK stubs.
     */
    @Suppress("UNCHECKED_CAST")
    private fun queryPackageOps(appOps: AppOpsManager): List<Any>? {
        return try {
            val method = AppOpsManager::class.java.getMethod(
                "getPackagesForOps",
                Array<String>::class.java,
            )
            val result = method.invoke(
                appOps,
                arrayOf(
                    AppOpsManager.OPSTR_CAMERA,
                    AppOpsManager.OPSTR_RECORD_AUDIO,
                    AppOpsManager.OPSTR_FINE_LOCATION,
                    AppOpsManager.OPSTR_COARSE_LOCATION,
                ),
            )
            result as? List<Any>
        } catch (_: SecurityException) {
            null
        } catch (error: Exception) {
            val cause = error.cause
            if (cause is SecurityException) null else null
        }
    }

    private fun packageNameOf(pkgOps: Any): String? = try {
        pkgOps.javaClass.getMethod("getPackageName").invoke(pkgOps) as? String
    } catch (_: Exception) {
        null
    }

    @Suppress("UNCHECKED_CAST")
    private fun opsOf(pkgOps: Any): List<Any>? = try {
        pkgOps.javaClass.getMethod("getOps").invoke(pkgOps) as? List<Any>
    } catch (_: Exception) {
        null
    }

    private fun opStrOf(op: Any): String? = try {
        op.javaClass.getMethod("getOpStr").invoke(op) as? String
    } catch (_: Exception) {
        null
    }

    private fun lastAccessTimeMs(op: Any): Long {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val t = op.javaClass
                    .getMethod("getLastAccessTime", Int::class.javaPrimitiveType)
                    .invoke(op, OP_FLAGS_ALL_TRUSTED) as? Long
                if (t != null && t > 0L) return t
            }
            @Suppress("DEPRECATION")
            (op.javaClass.getMethod("getTime").invoke(op) as? Long) ?: 0L
        } catch (_: Exception) {
            0L
        }
    }

    private data class TimedAccess(val packageName: String, val atMs: Long)

    private fun toEntry(context: Context, access: TimedAccess?): PrivacyAccessEntry {
        if (access == null) return PrivacyAccessEntry(null, null)
        val label = appLabel(context, access.packageName) ?: return PrivacyAccessEntry(null, null)
        return PrivacyAccessEntry(
            appName = label,
            whenLabel = relativeWhen(access.atMs),
        )
    }

    private fun appLabel(context: Context, packageName: String): String? {
        return try {
            val pm = context.packageManager
            val info = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(info)?.toString()?.takeIf { it.isNotBlank() }
        } catch (_: PackageManager.NameNotFoundException) {
            null
        } catch (_: Exception) {
            null
        }
    }

    private fun relativeWhen(atMs: Long): String {
        val now = System.currentTimeMillis()
        if (atMs <= 0L || atMs > now + 60_000L) return ""
        val delta = (now - atMs).coerceAtLeast(0L)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(delta)
        val hours = TimeUnit.MILLISECONDS.toHours(delta)
        return when {
            minutes < 1L -> "Just now"
            minutes < 60L -> "$minutes min ago"
            hours < 24L -> {
                val h = hours.toInt()
                if (h == 1) "1h ago" else "${h}h ago"
            }
            isSameLocalDay(atMs, now) -> "Today"
            isYesterday(atMs, now) -> "Yesterday"
            else -> {
                val days = TimeUnit.MILLISECONDS.toDays(delta).toInt().coerceAtLeast(1)
                if (days == 1) "1 day ago" else "$days days ago"
            }
        }
    }

    private fun isSameLocalDay(a: Long, b: Long): Boolean {
        val ca = Calendar.getInstance().apply { timeInMillis = a }
        val cb = Calendar.getInstance().apply { timeInMillis = b }
        return ca.get(Calendar.YEAR) == cb.get(Calendar.YEAR) &&
            ca.get(Calendar.DAY_OF_YEAR) == cb.get(Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(atMs: Long, now: Long): Boolean {
        val yesterday = Calendar.getInstance().apply {
            timeInMillis = now
            add(Calendar.DAY_OF_YEAR, -1)
        }
        val then = Calendar.getInstance().apply { timeInMillis = atMs }
        return yesterday.get(Calendar.YEAR) == then.get(Calendar.YEAR) &&
            yesterday.get(Calendar.DAY_OF_YEAR) == then.get(Calendar.DAY_OF_YEAR)
    }
}
