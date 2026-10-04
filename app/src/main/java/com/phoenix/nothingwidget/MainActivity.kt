package com.phoenix.nothingwidget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.phoenix.nothingwidget.ui.MainScreen
import com.phoenix.nothingwidget.ui.data.ThemePreferences
import com.phoenix.nothingwidget.ui.theme.AppTheme
import com.phoenix.nothingwidget.ui.theme.AppThemeMode
import com.phoenix.nothingwidget.ui.theme.NothingWidgetTheme
import com.phoenix.nothingwidget.widgets.screen_time.ScreenTimeRepository
import com.phoenix.nothingwidget.widgets.screen_time.ScreenTimeWidgetReceiver
import com.phoenix.nothingwidget.widgets.screen_time_large.ScreenTimeLargeWidgetReceiver
import com.phoenix.nothingwidget.widgets.weather.WeatherLocationHelper
import com.phoenix.nothingwidget.widgets.weather.WeatherWidgetReceiver
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onStart() {
        super.onStart()
        if (WeatherLocationHelper.hasLocationPermission(this)) {
            WeatherWidgetReceiver.captureLocationThenRefresh(this)
        }
        if (ScreenTimeRepository.hasUsageAccess(this)) {
            ScreenTimeWidgetReceiver.requestRefresh(this)
            ScreenTimeLargeWidgetReceiver.requestRefresh(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            val themeFlow = remember { ThemePreferences.themeModeFlow(context) }
            val themeMode by themeFlow.collectAsState(initial = AppThemeMode.NOTHING_PREMIUM)

            NothingWidgetTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AppTheme.colors.background,
                ) {
                    MainScreen(
                        themeMode = themeMode,
                        onThemeModeChange = { mode ->
                            scope.launch {
                                ThemePreferences.setThemeMode(context, mode)
                            }
                        },
                    )
                }
            }
        }
    }
}
