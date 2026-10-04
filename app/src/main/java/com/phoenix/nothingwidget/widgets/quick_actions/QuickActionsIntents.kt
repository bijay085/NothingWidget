package com.phoenix.nothingwidget.widgets.quick_actions

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.CalendarContract
import android.provider.MediaStore
import android.provider.Settings

/** Resolves launch intents for each [QuickActionType] without CameraX. */
object QuickActionsIntents {

    fun forAction(context: Context, action: QuickActionType): Intent? =
        when (action) {
            QuickActionType.QR_SCANNER -> qrScanner(context)
            QuickActionType.CAMERA -> camera(context)
            QuickActionType.MAPS -> maps(context)
            QuickActionType.NOTES -> notes(context)
            QuickActionType.CALENDAR -> calendar(context)
            QuickActionType.SCREENSHOT -> screenshot(context)
            QuickActionType.SCREEN_RECORDER -> screenRecorder(context)
            QuickActionType.CALCULATOR -> calculator(context)
            QuickActionType.PHONE -> phone()
            QuickActionType.SETTINGS -> settings()
            QuickActionType.FLASHLIGHT -> null // handled in-receiver
        }

    fun qrScanner(context: Context): Intent {
        val pm = context.packageManager
        val candidates = listOf(
            Intent("com.google.zxing.client.android.SCAN").apply {
                putExtra("SCAN_MODE", "QR_CODE_MODE")
            },
            Intent(Intent.ACTION_VIEW, Uri.parse("google://lens")),
            Intent(Intent.ACTION_VIEW, Uri.parse("https://lens.google.com")),
            Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA),
        )
        return firstResolvable(pm, candidates)
            ?: Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    fun camera(context: Context): Intent {
        val pm = context.packageManager
        val still = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (still.resolveActivity(pm) != null) return still

        val capture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (capture.resolveActivity(pm) != null) return capture

        return still
    }

    fun maps(context: Context): Intent {
        val pm = context.packageManager
        val geo = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (geo.resolveActivity(pm) != null) return geo

        val maps = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (maps.resolveActivity(pm) != null) return maps

        return Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_APP_MAPS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    fun notes(context: Context): Intent {
        val pm = context.packageManager
        val packages = listOf(
            "com.google.android.keep",
            "com.samsung.android.app.notes",
            "com.samsung.android.app.memo",
            "com.miui.notes",
            "com.nothing.notes",
            "com.simplemobiletools.notes.pro",
        )
        for (pkg in packages) {
            val launch = pm.getLaunchIntentForPackage(pkg)?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (launch != null) return launch
        }
        return Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    fun calendar(context: Context): Intent {
        val pm = context.packageManager
        val view = Intent(Intent.ACTION_VIEW).apply {
            data = CalendarContract.CONTENT_URI
                .buildUpon()
                .appendPath("time")
                .appendPath(System.currentTimeMillis().toString())
                .build()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (view.resolveActivity(pm) != null) return view

        return Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_APP_CALENDAR)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    fun screenshot(context: Context): Intent {
        val pm = context.packageManager
        val candidates = listOf(
            Intent("android.intent.action.CAPTURE_SCREENSHOT"),
            Intent("com.android.systemui.action.SCREENSHOT"),
        )
        return firstResolvable(pm, candidates)
            ?: Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    fun screenRecorder(context: Context): Intent {
        val pm = context.packageManager
        val packages = listOf(
            "com.samsung.android.app.smartcapture",
            "com.miui.screenrecorder",
            "com.coloros.screenrecorder",
            "com.oneplus.screenrecord",
            "com.nothing.screenshot",
        )
        for (pkg in packages) {
            val launch = pm.getLaunchIntentForPackage(pkg)?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (launch != null) return launch
        }
        val candidates = listOf(
            Intent("android.intent.action.SCREEN_RECORD"),
            Intent("com.android.systemui.screenrecord.START"),
        )
        return firstResolvable(pm, candidates)
            ?: Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    fun calculator(context: Context): Intent {
        val pm = context.packageManager
        val category = Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_APP_CALCULATOR)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (category.resolveActivity(pm) != null) return category

        val packages = listOf(
            "com.google.android.calculator",
            "com.android.calculator2",
            "com.sec.android.app.popupcalculator",
            "com.miui.calculator",
            "com.nothing.calculator",
            "com.oneplus.calculator",
        )
        for (pkg in packages) {
            val launch = pm.getLaunchIntentForPackage(pkg)?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (launch != null) return launch
        }
        return category
    }

    fun phone(): Intent =
        Intent(Intent.ACTION_DIAL).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    fun settings(): Intent =
        Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    private fun firstResolvable(pm: PackageManager, intents: List<Intent>): Intent? {
        for (intent in intents) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (intent.resolveActivity(pm) != null) return intent
        }
        return null
    }
}
