package com.phoenix.nothingwidget.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.phoenix.nothingwidget.widgets.weather.WeatherLocationHelper
import com.phoenix.nothingwidget.widgets.weather.WeatherWidgetReceiver

@Composable
fun rememberWeatherLocationPermissionRequester(): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { grants ->
        if (grants.values.any { granted -> granted }) {
            WeatherWidgetReceiver.captureLocationThenRefresh(context)
        } else {
            WeatherWidgetReceiver.requestRefresh(context)
        }
    }
    return remember(launcher) {
        {
            launcher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ),
            )
        }
    }
}

/**
 * Ask once on app open so city is in DataStore before / when the Weather widget is added.
 * When already granted, MainActivity.onStart performs the capture.
 */
@Composable
fun WeatherLocationBootstrap() {
    val context = LocalContext.current
    val requestPermission = rememberWeatherLocationPermissionRequester()
    var asked by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (asked) return@LaunchedEffect
        asked = true
        if (!WeatherLocationHelper.hasLocationPermission(context)) {
            requestPermission()
        }
    }
}
