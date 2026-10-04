package com.phoenix.nothingwidget.ui.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.phoenix.nothingwidget.core.settings.SettingsRepository
import com.phoenix.nothingwidget.core.settings.TemperatureUnit
import com.phoenix.nothingwidget.core.settings.TimeFormat
import com.phoenix.nothingwidget.ui.components.BackButton
import com.phoenix.nothingwidget.ui.components.cardDecoration
import com.phoenix.nothingwidget.ui.theme.AppTheme
import com.phoenix.nothingwidget.widgets.weather.WeatherLocationHelper
import com.phoenix.nothingwidget.widgets.weather.WeatherWidgetReceiver
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val dimensions = AppTheme.dimensions

    val timeFormat by SettingsRepository.timeFormatFlow(context)
        .collectAsState(initial = TimeFormat.HOUR_12)
    val temperatureUnit by SettingsRepository.temperatureUnitFlow(context)
        .collectAsState(initial = TemperatureUnit.CELSIUS)
    var locationGranted by remember {
        mutableStateOf(WeatherLocationHelper.hasLocationPermission(context))
    }
    val locationState = LocationPermissionUiState.fromGranted(locationGranted)

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                locationGranted = WeatherLocationHelper.hasLocationPermission(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensions.medium),
    ) {
        BackButton(
            onClick = onBack,
            modifier = Modifier.padding(bottom = dimensions.small),
        )

        Text(
            text = "Settings",
            style = typography.title,
            color = colors.textPrimary,
        )

        Spacer(modifier = Modifier.height(dimensions.small))

        SettingsCard {
            Text(
                text = "Time Format",
                style = typography.cardTitle,
                color = colors.textPrimary,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Choose clock display format",
                style = typography.subtitle,
                color = colors.textSecondary,
            )
            Spacer(modifier = Modifier.height(16.dp))

            SettingsRadioOption(
                label = TimeFormat.HOUR_12.label,
                selected = timeFormat == TimeFormat.HOUR_12,
                onClick = {
                    scope.launch {
                        SettingsRepository.setTimeFormat(context, TimeFormat.HOUR_12)
                    }
                },
            )
            Spacer(modifier = Modifier.height(8.dp))
            SettingsRadioOption(
                label = TimeFormat.HOUR_24.label,
                selected = timeFormat == TimeFormat.HOUR_24,
                onClick = {
                    scope.launch {
                        SettingsRepository.setTimeFormat(context, TimeFormat.HOUR_24)
                    }
                },
            )
        }

        SettingsCard {
            Text(
                text = "Temperature Unit",
                style = typography.cardTitle,
                color = colors.textPrimary,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Choose Weather widget temperature unit",
                style = typography.subtitle,
                color = colors.textSecondary,
            )
            Spacer(modifier = Modifier.height(16.dp))

            SettingsRadioOption(
                label = TemperatureUnit.CELSIUS.label,
                selected = temperatureUnit == TemperatureUnit.CELSIUS,
                onClick = {
                    scope.launch {
                        SettingsRepository.setTemperatureUnit(context, TemperatureUnit.CELSIUS)
                        WeatherWidgetReceiver.pushCurrent(context)
                    }
                },
            )
            Spacer(modifier = Modifier.height(8.dp))
            SettingsRadioOption(
                label = TemperatureUnit.FAHRENHEIT.label,
                selected = temperatureUnit == TemperatureUnit.FAHRENHEIT,
                onClick = {
                    scope.launch {
                        SettingsRepository.setTemperatureUnit(context, TemperatureUnit.FAHRENHEIT)
                        WeatherWidgetReceiver.pushCurrent(context)
                    }
                },
            )
        }

        SettingsCard {
            Text(
                text = "Permissions",
                style = typography.cardTitle,
                color = colors.textPrimary,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Location",
                style = typography.cardTitle,
                color = colors.textPrimary,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = locationState.label,
                style = typography.tag,
                color = if (locationState == LocationPermissionUiState.ALLOWED) {
                    colors.primary
                } else {
                    colors.textMuted
                },
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Open Settings",
                style = typography.chip,
                color = colors.onPrimary,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(colors.primary)
                    .clickable {
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null),
                        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            )
        }
    }
}

private enum class LocationPermissionUiState(val label: String) {
    ALLOWED("Allowed"),
    NOT_ALLOWED("Not allowed"),
    ;

    companion object {
        fun fromGranted(granted: Boolean): LocationPermissionUiState {
            return if (granted) ALLOWED else NOT_ALLOWED
        }
    }
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    val colors = AppTheme.colors
    val shape = RoundedCornerShape(20.dp)
    val elevation = if (colors.isDark) 4.dp else 6.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = colors.shadow.copy(alpha = if (colors.isDark) 0.20f else 0.08f),
                spotColor = colors.shadow.copy(alpha = if (colors.isDark) 0.26f else 0.12f),
            )
            .clip(shape)
            .background(colors.card)
            .cardDecoration()
            .border(1.dp, colors.cardBorder, shape)
            .padding(16.dp),
    ) {
        content()
    }
}

@Composable
private fun SettingsRadioOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(20.dp)
                .border(
                    width = 2.dp,
                    color = if (selected) colors.primary else colors.textMuted,
                    shape = CircleShape,
                ),
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(colors.primary),
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = typography.subtitle,
            color = if (selected) colors.primary else colors.textPrimary,
        )
    }
}
