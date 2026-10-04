package com.phoenix.nothingwidget.widgets.quick_actions

import androidx.annotation.DrawableRes
import com.phoenix.nothingwidget.R
import com.phoenix.nothingwidget.core.widget_config.IconStyleChoice

/**
 * App-controlled catalog of Quick Actions.
 * Add new entries here (e.g. ADD_WIFI): customization picks them up via [choices].
 */
enum class QuickActionType(
    val key: String,
    /** Short label on the 4×1 widget. */
    val label: String,
    /** Full name in Customize chips. */
    val choiceLabel: String,
    @param:DrawableRes val iconRes: Int,
) {
    QR_SCANNER("qr_scanner", "QR Scan", "QR Scanner", R.drawable.quick_actions_ic_qr),
    FLASHLIGHT("flashlight", "Flash", "Flashlight", R.drawable.quick_actions_ic_flashlight),
    CAMERA("camera", "Camera", "Camera", R.drawable.quick_actions_ic_camera),
    SCREENSHOT("screenshot", "Shot", "Screenshot", R.drawable.quick_actions_ic_screenshot),
    SCREEN_RECORDER("screen_recorder", "Record", "Screen Recorder", R.drawable.quick_actions_ic_record),
    CALCULATOR("calculator", "Calc", "Calculator", R.drawable.quick_actions_ic_calculator),
    NOTES("notes", "Notes", "Notes", R.drawable.quick_actions_ic_notes),
    CALENDAR("calendar", "Calendar", "Calendar", R.drawable.quick_actions_ic_calendar),
    MAPS("maps", "Maps", "Maps", R.drawable.quick_actions_ic_maps),
    PHONE("phone", "Phone", "Phone", R.drawable.quick_actions_ic_phone),
    SETTINGS("settings", "Settings", "Settings", R.drawable.quick_actions_ic_settings),
    ;

    companion object {
        fun fromKey(key: String?): QuickActionType =
            entries.firstOrNull { it.key.equals(key, ignoreCase = true) } ?: QR_SCANNER

        /** Only these actions appear in Customize: never open-ended device shortcuts. */
        fun choices(): List<IconStyleChoice> =
            entries.map { IconStyleChoice(it.choiceLabel, it.key) }
    }
}
