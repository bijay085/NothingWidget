package com.phoenix.nothingwidget.widgets.quick_actions

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat

/**
 * 4×1 Quick Actions widget — three slots from [QuickActionType].
 */
class QuickActionsWidgetReceiver : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            QuickActionsConfig.ACTION_RUN -> {
                val key = intent.getStringExtra(QuickActionsConfig.EXTRA_ACTION)
                runAction(context, QuickActionType.fromKey(key))
            }
            // Legacy broadcasts from the first release.
            "com.phoenix.nothingwidget.widgets.quick_actions.ACTION_QR" ->
                runAction(context, QuickActionType.QR_SCANNER)
            "com.phoenix.nothingwidget.widgets.quick_actions.ACTION_FLASHLIGHT" ->
                runAction(context, QuickActionType.FLASHLIGHT)
            "com.phoenix.nothingwidget.widgets.quick_actions.ACTION_CAMERA" ->
                runAction(context, QuickActionType.CAMERA)
            else -> super.onReceive(context, intent)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        try {
            QuickActionsRenderer.render(context, appWidgetManager, appWidgetIds)
        } catch (error: Exception) {
            Log.e(TAG, "Quick Actions update failed", error)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: android.os.Bundle?,
    ) {
        try {
            QuickActionsRenderer.render(context, appWidgetManager, intArrayOf(appWidgetId))
        } catch (error: Exception) {
            Log.e(TAG, "Quick Actions resize update failed", error)
        }
    }

    private fun runAction(context: Context, action: QuickActionType) {
        when (action) {
            QuickActionType.FLASHLIGHT -> toggleFlashlight(context)
            else -> openIntent(context, action)
        }
    }

    private fun openIntent(context: Context, action: QuickActionType) {
        try {
            val launch = QuickActionsIntents.forAction(context, action)
            if (launch == null) {
                toast(context, "Action unavailable")
                return
            }
            context.startActivity(launch)
        } catch (error: Exception) {
            Log.e(TAG, "Action failed: ${action.key}", error)
            toast(context, "Couldn’t open ${action.choiceLabel}")
        }
    }

    private fun toggleFlashlight(context: Context) {
        val app = context.applicationContext
        val granted = ContextCompat.checkSelfPermission(
            app,
            android.Manifest.permission.CAMERA,
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (!granted) {
            try {
                app.startActivity(QuickActionsPermissionActivity.intent(app))
            } catch (error: Exception) {
                Log.e(TAG, "Permission activity failed", error)
                toast(app, "Allow Camera to use the flashlight")
            }
            return
        }

        val ok = QuickActionsFlashlight.toggle(app)
        if (!ok) toast(app, "Flashlight unavailable")
        QuickActionsRenderer.renderAll(app)
    }

    private fun toast(context: Context, message: String) {
        Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "QuickActionsWidget"
    }
}
