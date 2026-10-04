package com.phoenix.nothingwidget.ui

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.phoenix.nothingwidget.widgets.screen_time.ScreenTimeRepository
import com.phoenix.nothingwidget.widgets.screen_time.ScreenTimeWidgetReceiver
import com.phoenix.nothingwidget.widgets.screen_time_large.ScreenTimeLargeWidgetReceiver

/** Opens the system Usage Access screen (PACKAGE_USAGE_STATS is not a runtime dialog). */
fun requestScreenTimeUsageAccess(context: Context) {
    try {
        context.startActivity(ScreenTimeRepository.usageAccessSettingsIntent())
        Toast.makeText(
            context,
            "Turn on usage access for Nothing Widget to show screen time.",
            Toast.LENGTH_LONG,
        ).show()
    } catch (_: Exception) {
        Toast.makeText(context, "Open Settings → Apps → Special access → Usage access.", Toast.LENGTH_LONG).show()
    }
}

@Composable
fun rememberScreenTimeUsageAccessRequester(): () -> Unit {
    val context = LocalContext.current
    return remember(context) {
        {
            requestScreenTimeUsageAccess(context)
            // Best-effort refresh after user returns; onStart also refreshes.
            ScreenTimeWidgetReceiver.requestRefresh(context)
            ScreenTimeLargeWidgetReceiver.requestRefresh(context)
        }
    }
}
