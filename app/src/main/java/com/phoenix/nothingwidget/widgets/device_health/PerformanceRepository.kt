package com.phoenix.nothingwidget.widgets.device_health

import android.app.ActivityManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import android.os.StatFs
import android.os.SystemClock
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.RandomAccessFile

/** CPU / RAM / storage / temp from local Android APIs: no internet. */
object PerformanceRepository {

    private const val PREFS = "device_health_perf"
    private const val KEY_IDLE = "cpu_idle"
    private const val KEY_TOTAL = "cpu_total"
    private const val KEY_CPUIDLE_SUM = "cpuidle_sum"
    private const val KEY_CPUIDLE_AT = "cpuidle_at"
    private const val KEY_CPUIDLE_CORES = "cpuidle_cores"
    private const val KEY_LAST_CPU = "cpu_last_pct"
    private const val GB = 1024.0 * 1024.0 * 1024.0
    private const val CPU_SYS = "/sys/devices/system/cpu"

    fun read(context: Context): DevicePerformance {
        val ram = readRam(context)
        val storage = readStorage()
        val cpu = sampleCpuPercent(context)
        val tempC = readCpuTemperatureC()
            ?: BatteryHealthRepository.temperatureCOrNull(context)

        return DevicePerformance(
            cpuUsage = if (cpu < 0) DEVICE_HEALTH_NA else "$cpu%",
            ramUsed = ram?.used ?: DEVICE_HEALTH_NA,
            ramTotal = ram?.total ?: DEVICE_HEALTH_NA,
            storageUsed = storage?.used ?: DEVICE_HEALTH_NA,
            storageTotal = storage?.total ?: DEVICE_HEALTH_NA,
            deviceTemperature = if (tempC == null) DEVICE_HEALTH_NA else "${tempC.toInt()}°C",
        )
    }

    private data class GbPair(val used: String, val total: String)

    private fun readRam(context: Context): GbPair? {
        return try {
            val am = context.getSystemService(ActivityManager::class.java) ?: return null
            val info = ActivityManager.MemoryInfo()
            am.getMemoryInfo(info)
            if (info.totalMem <= 0L) return null
            val totalGb = info.totalMem / GB
            val usedGb = ((info.totalMem - info.availMem).coerceAtLeast(0L)) / GB
            GbPair(
                used = String.format("%.1f", usedGb),
                total = String.format("%.0f", totalGb),
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun readStorage(): GbPair? {
        return try {
            val stat = StatFs(Environment.getDataDirectory().absolutePath)
            val total = stat.totalBytes
            if (total <= 0L) return null
            val used = (total - stat.availableBytes).coerceAtLeast(0L)
            GbPair(
                used = String.format("%.0f", used / GB),
                total = String.format("%.0f", total / GB),
            )
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Prefer /proc/stat when readable; on Nothing/Android 16 apps are denied, so use
     * cpuidle state time deltas (readable under /sys). Fallback: frequency ratio, then cache.
     */
    private fun sampleCpuPercent(context: Context): Int {
        sampleCpuFromProc(context).takeIf { it >= 0 }?.let {
            cacheCpu(context, it)
            return it
        }
        sampleCpuFromCpuidle(context).takeIf { it >= 0 }?.let {
            cacheCpu(context, it)
            return it
        }
        sampleCpuFromFreq()?.let {
            cacheCpu(context, it)
            return it
        }
        return prefs(context).getInt(KEY_LAST_CPU, -1)
    }

    private fun cacheCpu(context: Context, pct: Int) {
        prefs(context).edit().putInt(KEY_LAST_CPU, pct).apply()
    }

    private fun sampleCpuFromProc(context: Context): Int {
        val sample = readProcStat() ?: return -1
        val store = prefs(context)
        val prevIdle = store.getLong(KEY_IDLE, -1L)
        val prevTotal = store.getLong(KEY_TOTAL, -1L)
        store.edit()
            .putLong(KEY_IDLE, sample.idle)
            .putLong(KEY_TOTAL, sample.total)
            .apply()
        if (prevIdle < 0L || prevTotal < 0L) return -1
        return percentBetween(ProcSample(prevIdle, prevTotal), sample)
    }

    private fun percentBetween(first: ProcSample, second: ProcSample): Int {
        val idleDelta = second.idle - first.idle
        val totalDelta = second.total - first.total
        if (totalDelta <= 0L) return -1
        val busy = (totalDelta - idleDelta).coerceAtLeast(0L)
        return ((busy * 100L) / totalDelta).toInt().coerceIn(0, 100)
    }

    /** busy% = 100 - (idle_us_delta / (wall_us * cores)) */
    private fun sampleCpuFromCpuidle(context: Context): Int {
        val snapshot = readCpuidleSnapshot() ?: return -1
        val now = SystemClock.elapsedRealtime()
        val store = prefs(context)
        val prevSum = store.getLong(KEY_CPUIDLE_SUM, -1L)
        val prevAt = store.getLong(KEY_CPUIDLE_AT, -1L)
        val prevCores = store.getInt(KEY_CPUIDLE_CORES, snapshot.cores)
        store.edit()
            .putLong(KEY_CPUIDLE_SUM, snapshot.idleSumUs)
            .putLong(KEY_CPUIDLE_AT, now)
            .putInt(KEY_CPUIDLE_CORES, snapshot.cores)
            .apply()

        if (prevSum < 0L || prevAt < 0L || now <= prevAt) return -1
        val wallUs = (now - prevAt) * 1000L
        val cores = minOf(snapshot.cores, prevCores).coerceAtLeast(1)
        val capacityUs = wallUs * cores
        if (capacityUs <= 0L) return -1
        val idleDelta = (snapshot.idleSumUs - prevSum).coerceAtLeast(0L).coerceAtMost(capacityUs)
        val busy = capacityUs - idleDelta
        return ((busy * 100L) / capacityUs).toInt().coerceIn(0, 100)
    }

    private data class CpuidleSnapshot(val idleSumUs: Long, val cores: Int)

    private fun readCpuidleSnapshot(): CpuidleSnapshot? {
        val online = readOnlineCpus()
        if (online.isEmpty()) return null
        var sum = 0L
        var counted = 0
        for (cpu in online) {
            val idleDir = File("$CPU_SYS/cpu$cpu/cpuidle")
            if (!idleDir.isDirectory) continue
            var cpuIdle = 0L
            val states = idleDir.listFiles()?.filter { it.name.startsWith("state") }.orEmpty()
            if (states.isEmpty()) continue
            for (state in states) {
                val time = File(state, "time").takeIf { it.canRead() }
                    ?.readText()?.trim()?.toLongOrNull() ?: continue
                cpuIdle += time
            }
            sum += cpuIdle
            counted++
        }
        if (counted == 0) return null
        return CpuidleSnapshot(idleSumUs = sum, cores = counted)
    }

    private fun readOnlineCpus(): List<Int> {
        return try {
            val text = File("$CPU_SYS/online").takeIf { it.canRead() }?.readText()?.trim()
                ?: return (0 until Runtime.getRuntime().availableProcessors()).toList()
            parseCpuList(text)
        } catch (_: Exception) {
            (0 until Runtime.getRuntime().availableProcessors()).toList()
        }
    }

    /** "0-3,6,7" → list of ints */
    private fun parseCpuList(text: String): List<Int> {
        val out = ArrayList<Int>()
        for (part in text.split(',')) {
            val p = part.trim()
            if (p.isEmpty()) continue
            if ('-' in p) {
                val ends = p.split('-')
                val a = ends.getOrNull(0)?.toIntOrNull() ?: continue
                val b = ends.getOrNull(1)?.toIntOrNull() ?: continue
                for (i in a..b) out.add(i)
            } else {
                p.toIntOrNull()?.let { out.add(it) }
            }
        }
        return out
    }

    /** Instantaneous frequency headroom when idle deltas are not ready yet. */
    private fun sampleCpuFromFreq(): Int? {
        val online = readOnlineCpus()
        if (online.isEmpty()) return null
        var sum = 0
        var n = 0
        for (cpu in online) {
            val cur = readLong("$CPU_SYS/cpu$cpu/cpufreq/scaling_cur_freq")
                ?: readLong("$CPU_SYS/cpu$cpu/cpufreq/cpuinfo_cur_freq")
                ?: continue
            val max = readLong("$CPU_SYS/cpu$cpu/cpufreq/cpuinfo_max_freq")
                ?: readLong("$CPU_SYS/cpu$cpu/cpufreq/scaling_max_freq")
                ?: continue
            if (max <= 0L) continue
            sum += ((cur * 100L) / max).toInt().coerceIn(0, 100)
            n++
        }
        return if (n == 0) null else (sum / n)
    }

    private fun readLong(path: String): Long? {
        return try {
            File(path).takeIf { it.canRead() }?.readText()?.trim()?.toLongOrNull()
        } catch (_: Exception) {
            null
        }
    }

    private data class ProcSample(val idle: Long, val total: Long)

    private fun readProcStat(): ProcSample? {
        return try {
            BufferedReader(FileReader("/proc/stat")).use { reader ->
                parseCpuLine(reader.readLine())
            }
        } catch (_: Exception) {
            try {
                RandomAccessFile("/proc/stat", "r").use { file ->
                    parseCpuLine(file.readLine())
                }
            } catch (_: Exception) {
                null
            }
        }
    }

    private fun parseCpuLine(line: String?): ProcSample? {
        if (line.isNullOrBlank()) return null
        val parts = line.trim().split(Regex("\\s+"))
        if (parts.size < 5 || parts[0] != "cpu") return null
        val values = parts.drop(1).mapNotNull { it.toLongOrNull() }
        if (values.size < 4) return null
        val idle = values[3] + values.getOrElse(4) { 0L }
        return ProcSample(idle = idle, total = values.sum())
    }

    private fun readCpuTemperatureC(): Float? {
        val root = File("/sys/class/thermal")
        if (!root.isDirectory) return null
        val zones = root.listFiles()?.filter { it.name.startsWith("thermal_zone") }.orEmpty()
        var best: Float? = null
        for (zone in zones) {
            try {
                val type = File(zone, "type").takeIf { it.canRead() }?.readText()?.trim().orEmpty()
                val raw = File(zone, "temp").takeIf { it.canRead() }?.readText()?.trim()?.toFloatOrNull()
                    ?: continue
                val celsius = if (raw > 200f) raw / 1000f else raw
                if (celsius < 5f || celsius > 95f) continue
                val preferred = type.contains("cpu", ignoreCase = true) ||
                    type.contains("skin", ignoreCase = true) ||
                    type.contains("soc", ignoreCase = true)
                if (preferred) return celsius
                if (best == null) best = celsius
            } catch (_: Exception) {
                // next zone
            }
        }
        return best
    }

    private fun prefs(context: Context): SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
