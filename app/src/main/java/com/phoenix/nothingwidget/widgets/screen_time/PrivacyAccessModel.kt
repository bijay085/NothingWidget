package com.phoenix.nothingwidget.widgets.screen_time

/** One privacy sensor row (camera / mic / location). */
data class PrivacyAccessEntry(
    val appName: String?,
    val whenLabel: String?,
) {
    val hasAccess: Boolean get() = !appName.isNullOrBlank()
}

data class PrivacyAccessModel(
    val camera: PrivacyAccessEntry,
    val microphone: PrivacyAccessEntry,
    val location: PrivacyAccessEntry,
) {
    companion object {
        fun empty(): PrivacyAccessModel = PrivacyAccessModel(
            camera = PrivacyAccessEntry(null, null),
            microphone = PrivacyAccessEntry(null, null),
            location = PrivacyAccessEntry(null, null),
        )
    }
}
