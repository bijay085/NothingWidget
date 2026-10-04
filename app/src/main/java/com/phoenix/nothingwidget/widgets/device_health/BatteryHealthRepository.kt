package com.phoenix.nothingwidget.widgets.device_health

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import java.io.File
import kotlin.math.abs
import kotlin.math.roundToInt

/** Battery stats from BatteryManager / sticky BATTERY_CHANGED: no special permissions. */
object BatteryHealthRepository {

    private const val EXTRA_CYCLE_COUNT = "android.os.extra.CYCLE_COUNT"
    private const val BATTERY_PROPERTY_CYCLE_COUNT = 9
    private const val BATTERY_PROPERTY_STATE_OF_HEALTH = 10

    fun read(context: Context): BatteryHealth {
        val intent = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED),
        ) ?: return unavailable()

        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100).coerceAtLeast(1)
        val percentage = if (level < 0) {
            -1
        } else {
            ((level * 100f) / scale).toInt().coerceIn(0, 100)
        }

        val status = intent.getIntExtra(
            BatteryManager.EXTRA_STATUS,
            BatteryManager.BATTERY_STATUS_UNKNOWN,
        )
        val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
        val statusSimple = statusSimpleLabel(status)
        val statusDetail = statusDetailLabel(status, plugged)

        val tempTenths = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Int.MIN_VALUE)
        val temperature = if (tempTenths == Int.MIN_VALUE) {
            DEVICE_HEALTH_NA
        } else {
            "${tempTenths / 10}°C"
        }

        val bm = context.getSystemService(BatteryManager::class.java)
        val health = healthPercentLabel(context, bm)
        val capacities = capacityPair(context, bm)

        val voltageMv = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
        val voltage = if (voltageMv > 0) {
            String.format("%.2f V", voltageMv / 1000f)
        } else {
            DEVICE_HEALTH_NA
        }

        val cycleCount = cycleCountLabel(intent, bm)
        val currentUa = currentMicroamps(bm)
        val power = powerLabel(currentUa, voltageMv)
        val current = currentLabel(currentUa)
        val timeToFull = timeToFullLabel(bm, status)
        val technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)
            ?.takeIf { it.isNotBlank() }
            ?: DEVICE_HEALTH_NA

        return BatteryHealth(
            percentage = percentage,
            statusSimple = statusSimple,
            statusDetail = statusDetail,
            temperature = temperature,
            health = health,
            cycleCount = cycleCount,
            voltage = voltage,
            power = power,
            current = current,
            timeToFull = timeToFull,
            capacityMah = capacities.first,
            designCapacityMah = capacities.second,
            technology = technology,
        )
    }

    fun percentageOrNa(context: Context): String {
        val p = read(context).percentage
        return if (p < 0) DEVICE_HEALTH_NA else "$p%"
    }

    fun temperatureCOrNull(context: Context): Float? {
        val intent = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED),
        ) ?: return null
        val tempTenths = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Int.MIN_VALUE)
        if (tempTenths == Int.MIN_VALUE) return null
        return tempTenths / 10f
    }

    private fun unavailable() = BatteryHealth(
        percentage = -1,
        statusSimple = DEVICE_HEALTH_NA,
        statusDetail = DEVICE_HEALTH_NA,
        temperature = DEVICE_HEALTH_NA,
        health = DEVICE_HEALTH_NA,
        cycleCount = DEVICE_HEALTH_NA,
        voltage = DEVICE_HEALTH_NA,
        power = DEVICE_HEALTH_NA,
        current = DEVICE_HEALTH_NA,
        timeToFull = DEVICE_HEALTH_NA,
        capacityMah = DEVICE_HEALTH_NA,
        designCapacityMah = DEVICE_HEALTH_NA,
        technology = DEVICE_HEALTH_NA,
    )

    private fun statusSimpleLabel(status: Int): String = when (status) {
        BatteryManager.BATTERY_STATUS_FULL -> "Full"
        BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
        BatteryManager.BATTERY_STATUS_DISCHARGING,
        BatteryManager.BATTERY_STATUS_NOT_CHARGING,
        -> "Not charging"
        else -> DEVICE_HEALTH_NA
    }

    private fun statusDetailLabel(status: Int, plugged: Int): String = when (status) {
        BatteryManager.BATTERY_STATUS_FULL -> "Full"
        BatteryManager.BATTERY_STATUS_CHARGING -> when (plugged) {
            BatteryManager.BATTERY_PLUGGED_AC -> "Fast Charging"
            BatteryManager.BATTERY_PLUGGED_USB -> "Normal Charging"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless Charging"
            else -> "Charging"
        }
        BatteryManager.BATTERY_STATUS_DISCHARGING,
        BatteryManager.BATTERY_STATUS_NOT_CHARGING,
        -> "Not charging"
        else -> DEVICE_HEALTH_NA
    }

    private fun healthPercentLabel(context: Context, bm: BatteryManager?): String {
        sysfsHealthPercent()?.let { return "$it%" }
        if (bm != null) {
            try {
                val soh = bm.getIntProperty(BATTERY_PROPERTY_STATE_OF_HEALTH)
                if (soh in 1..100) return "$soh%"
            } catch (_: Exception) {
                // calculate below
            }
        }
        calculatedChargeFullUah(bm)?.let { fullUah ->
            val designMah = designCapacityMah(context) ?: return@let
            if (designMah <= 0.0) return@let
            val designUah = (designMah * 1000.0).toLong().coerceAtLeast(1L)
            val pct = ((fullUah * 100L) / designUah).toInt().coerceIn(0, 100)
            return "$pct%"
        }
        return DEVICE_HEALTH_NA
    }

    private fun capacityPair(context: Context, bm: BatteryManager?): Pair<String, String> {
        val designMah = designCapacityMah(context)
        val designLabel = if (designMah != null && designMah > 0.0) {
            "${designMah.roundToInt()} mAh"
        } else {
            DEVICE_HEALTH_NA
        }

        val fullUah = sysfsChargeFullUah() ?: calculatedChargeFullUah(bm)
        val capacityLabel = if (fullUah != null && fullUah > 0L) {
            "${(fullUah / 1000.0).roundToInt()} mAh"
        } else {
            DEVICE_HEALTH_NA
        }
        return capacityLabel to designLabel
    }

    private fun sysfsHealthPercent(): Int? {
        val full = sysfsChargeFullUah() ?: return null
        val design = readPositiveLong(
            "/sys/class/power_supply/battery/charge_full_design",
            "/sys/class/power_supply/bms/charge_full_design",
            "/sys/class/power_supply/Battery/charge_full_design",
        ) ?: return null
        if (design <= 0L) return null
        return ((full * 100L) / design).toInt().coerceIn(0, 100)
    }

    private fun sysfsChargeFullUah(): Long? = readPositiveLong(
        "/sys/class/power_supply/battery/charge_full",
        "/sys/class/power_supply/bms/charge_full",
        "/sys/class/power_supply/Battery/charge_full",
    )

    private fun calculatedChargeFullUah(bm: BatteryManager?): Long? {
        if (bm == null) return null
        val counterUah = bm.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
        val capacityPct = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        if (counterUah <= 0L || counterUah == Long.MIN_VALUE) return null
        if (capacityPct <= 0 || capacityPct == Int.MIN_VALUE) return null
        return (counterUah * 100L) / capacityPct
    }

    private fun designCapacityMah(context: Context): Double? {
        return try {
            val clazz = Class.forName("com.android.internal.os.PowerProfile")
            val profile = clazz.getConstructor(Context::class.java).newInstance(context)
            val mah = clazz.getMethod("getBatteryCapacity").invoke(profile) as? Double
            if (mah != null && mah > 0.0) mah else null
        } catch (_: Exception) {
            null
        }
    }

    private fun readPositiveLong(vararg paths: String): Long? {
        for (path in paths) {
            try {
                val raw = File(path).takeIf { it.canRead() }?.readText()?.trim()
                val value = raw?.toLongOrNull()
                if (value != null && value > 0L) return value
            } catch (_: Exception) {
                // try next
            }
        }
        return null
    }

    private fun cycleCountLabel(intent: Intent, bm: BatteryManager?): String {
        val fromIntent = intent.getIntExtra(EXTRA_CYCLE_COUNT, -1)
        if (fromIntent >= 0) return fromIntent.toString()
        if (bm != null) {
            try {
                val cycles = bm.getIntProperty(BATTERY_PROPERTY_CYCLE_COUNT)
                if (cycles >= 0 && cycles != Int.MIN_VALUE) return cycles.toString()
            } catch (_: Exception) {
                // fall through
            }
        }
        return cycleCountFromSysfs() ?: DEVICE_HEALTH_NA
    }

    private fun cycleCountFromSysfs(): String? {
        val paths = arrayOf(
            "/sys/class/power_supply/battery/cycle_count",
            "/sys/class/power_supply/bms/cycle_count",
            "/sys/class/power_supply/Battery/cycle_count",
        )
        for (path in paths) {
            try {
                val raw = File(path).takeIf { it.canRead() }?.readText()?.trim()
                val cycles = raw?.toIntOrNull()
                if (cycles != null && cycles >= 0) return cycles.toString()
            } catch (_: Exception) {
                // try next
            }
        }
        return null
    }

    private fun currentMicroamps(bm: BatteryManager?): Long? {
        if (bm == null) return null
        return try {
            val ua = bm.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
            if (ua == Long.MIN_VALUE || ua == 0L) null else ua
        } catch (_: Exception) {
            null
        }
    }

    private fun currentLabel(ua: Long?): String {
        if (ua == null) return DEVICE_HEALTH_NA
        val ma = abs(ua / 1000.0).roundToInt()
        return if (ma <= 0) DEVICE_HEALTH_NA else "$ma mA"
    }

    private fun powerLabel(ua: Long?, voltageMv: Int): String {
        if (ua == null || voltageMv <= 0) return DEVICE_HEALTH_NA
        val watts = abs(ua / 1_000_000.0) * (voltageMv / 1000.0)
        return if (watts < 0.01) DEVICE_HEALTH_NA else String.format("%.0f W", watts)
    }

    private fun timeToFullLabel(bm: BatteryManager?, status: Int): String {
        if (bm == null) return DEVICE_HEALTH_NA
        if (status != BatteryManager.BATTERY_STATUS_CHARGING) return DEVICE_HEALTH_NA
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return DEVICE_HEALTH_NA
        return try {
            val ms = bm.computeChargeTimeRemaining()
            if (ms <= 0L || ms == Long.MIN_VALUE) return DEVICE_HEALTH_NA
            val minutes = ((ms + 59_999L) / 60_000L).toInt().coerceAtLeast(1)
            if (minutes >= 60) {
                val h = minutes / 60
                val m = minutes % 60
                if (m == 0) "${h}h" else "${h}h ${m}m"
            } else {
                "$minutes min"
            }
        } catch (_: Exception) {
            DEVICE_HEALTH_NA
        }
    }
}
