package com.phoenix.nothingwidget.widgets.device_health

const val DEVICE_HEALTH_NA = "N/A"

data class DeviceHealthModel(
    val battery: BatteryHealth,
    val performance: DevicePerformance,
)

data class BatteryHealth(
    /** Current charge 0-100, or -1 if unknown. */
    val percentage: Int,
    /** Overview status: Charging / Not charging / Full. */
    val statusSimple: String,
    /** Charging page status: Fast charging / Wireless / etc. */
    val statusDetail: String,
    val temperature: String,
    val health: String,
    val cycleCount: String,
    val voltage: String,
    val power: String,
    val current: String,
    val timeToFull: String,
    val capacityMah: String,
    val designCapacityMah: String,
    val technology: String,
)

data class DevicePerformance(
    val cpuUsage: String,
    val ramUsed: String,
    val ramTotal: String,
    val storageUsed: String,
    val storageTotal: String,
    val deviceTemperature: String,
) {
    val ramLine: String
        get() = if (ramUsed == DEVICE_HEALTH_NA || ramTotal == DEVICE_HEALTH_NA) {
            DEVICE_HEALTH_NA
        } else {
            "$ramUsed / $ramTotal GB"
        }

    val storageLine: String
        get() = if (storageUsed == DEVICE_HEALTH_NA || storageTotal == DEVICE_HEALTH_NA) {
            DEVICE_HEALTH_NA
        } else {
            "$storageUsed / $storageTotal GB"
        }
}
