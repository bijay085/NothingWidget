package com.phoenix.nothingwidget.ui.customization

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomization
import com.phoenix.nothingwidget.ui.components.BackgroundPattern
import com.phoenix.nothingwidget.ui.data.ThemePreferences
import com.phoenix.nothingwidget.ui.theme.AppTheme
import com.phoenix.nothingwidget.ui.theme.AppThemeMode
import com.phoenix.nothingwidget.ui.theme.NothingWidgetTheme

/**
 * Shared AppWidget configuration activity: opens the existing customization screen
 * for the widget the launcher asked to configure.
 *
 * Result is always RESULT_OK with the widget id so a back-press during the first
 * add never makes the launcher discard the widget. Settings are stored per widget
 * type (existing DataStore model), then every instance is refreshed.
 */
abstract class WidgetConfigActivity : ComponentActivity() {

    protected abstract val widgetName: String
    protected abstract val customization: WidgetCustomization
    protected abstract val previewCircular: Boolean

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        // Default until the user finishes; cancelled if launched without a valid id.
        setResult(RESULT_CANCELED, resultIntent())
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        enableEdgeToEdge()
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() = finishWithOk()
            },
        )

        setContent {
            val context = LocalContext.current
            val themeFlow = remember { ThemePreferences.themeModeFlow(context) }
            val themeMode by themeFlow.collectAsState(initial = AppThemeMode.NOTHING_PREMIUM)

            NothingWidgetTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AppTheme.colors.background,
                ) {
                    ConfigHost {
                        WidgetCustomizationScreen(
                            widgetName = widgetName,
                            previewCircular = previewCircular,
                            customization = customization,
                            onBack = ::finishWithOk,
                            onApplied = ::finishWithOk,
                        )
                    }
                }
            }
        }
    }

    private fun resultIntent(): Intent =
        Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

    private fun finishWithOk() {
        // Ensure the (possibly just-added) widget renders with the saved style.
        customization.refresh(applicationContext)
        setResult(RESULT_OK, resultIntent())
        finish()
    }
}

@androidx.compose.runtime.Composable
private fun ConfigHost(content: @androidx.compose.runtime.Composable () -> Unit) {
    val colors = AppTheme.colors
    val dimensions = AppTheme.dimensions

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(colors.background, colors.backgroundMuted),
                ),
            ),
    ) {
        BackgroundPattern()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimensions.large)
                .padding(
                    top = dimensions.large,
                    bottom = dimensions.large + dimensions.medium,
                ),
        ) {
            content()
        }
    }
}
