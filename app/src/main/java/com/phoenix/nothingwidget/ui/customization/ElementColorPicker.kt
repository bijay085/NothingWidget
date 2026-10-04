package com.phoenix.nothingwidget.ui.customization

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.phoenix.nothingwidget.core.widget_config.ColorChoice
import com.phoenix.nothingwidget.ui.theme.AppTheme

/**
 * Reusable per-element color control.
 * Preset chips stay inline; selecting Custom opens the advanced picker.
 */
@Composable
fun ElementColorPicker(
    color: Int,
    presets: List<ColorChoice>,
    onColorChange: (Int) -> Unit,
) {
    var showAdvanced by remember { mutableStateOf(false) }
    val customSelected = presets.none { sameRgb(it.color, color) }

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        presets.forEach { choice ->
            ColorPresetChip(
                label = choice.label,
                swatch = choice.color,
                selected = sameRgb(choice.color, color),
                onClick = { onColorChange(choice.color) },
            )
        }
        ColorPresetChip(
            label = "Custom",
            swatch = if (customSelected) color else 0xFF888888.toInt(),
            selected = customSelected,
            onClick = { showAdvanced = true },
        )
    }

    if (showAdvanced) {
        AdvancedColorPickerDialog(
            color = color,
            onColorChange = onColorChange,
            onDismiss = { showAdvanced = false },
        )
    }
}

/** One reusable advanced picker — only shown when Custom is selected. */
@Composable
private fun AdvancedColorPickerDialog(
    color: Int,
    onColorChange: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val hsv = remember { FloatArray(3) }
    var hue by remember { mutableFloatStateOf(0f) }
    var saturation by remember { mutableFloatStateOf(1f) }
    var brightness by remember { mutableFloatStateOf(1f) }
    var hexText by remember { mutableStateOf(toHex(color)) }

    LaunchedEffect(color) {
        val fromSliders = AndroidColor.HSVToColor(floatArrayOf(hue, saturation, brightness))
        if (!sameRgb(fromSliders, color)) {
            AndroidColor.colorToHSV(color or 0xFF000000.toInt(), hsv)
            hue = hsv[0]
            saturation = hsv[1]
            brightness = hsv[2]
        }
        hexText = toHex(color)
    }

    fun emit(h: Float = hue, s: Float = saturation, v: Float = brightness) {
        val next = AndroidColor.HSVToColor(floatArrayOf(h, s, v))
        hexText = toHex(next)
        onColorChange(next)
    }

    val preview = Color(AndroidColor.HSVToColor(floatArrayOf(hue, saturation, brightness)))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Custom Color", style = typography.cardTitle, color = colors.textPrimary)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(preview)
                            .border(1.dp, colors.textMuted, CircleShape),
                    )
                    HexField(
                        value = hexText,
                        onValueChange = { raw ->
                            val cleaned = raw.filter { it.isLetterOrDigit() }.take(6).uppercase()
                            hexText = cleaned
                            if (cleaned.length == 6) {
                                runCatching {
                                    val parsed = AndroidColor.parseColor("#$cleaned")
                                    AndroidColor.colorToHSV(parsed, hsv)
                                    hue = hsv[0]
                                    saturation = hsv[1]
                                    brightness = hsv[2]
                                    onColorChange(parsed)
                                }
                            }
                        },
                    )
                }

                SliderRow(label = "Hue") {
                    Box(contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                                .height(10.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(Brush.horizontalGradient(HUE_STOPS)),
                        )
                        Slider(
                            value = hue,
                            onValueChange = {
                                hue = it
                                emit(h = it)
                            },
                            valueRange = 0f..360f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color.White,
                                activeTrackColor = Color.Transparent,
                                inactiveTrackColor = Color.Transparent,
                            ),
                        )
                    }
                }

                SliderRow(label = "Saturation") {
                    Slider(
                        value = saturation,
                        onValueChange = {
                            saturation = it
                            emit(s = it)
                        },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = colors.primary,
                            activeTrackColor = colors.primary,
                            inactiveTrackColor = colors.cardBorder,
                        ),
                    )
                }

                SliderRow(label = "Brightness") {
                    Slider(
                        value = brightness,
                        onValueChange = {
                            brightness = it
                            emit(v = it)
                        },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = colors.primary,
                            activeTrackColor = colors.primary,
                            inactiveTrackColor = colors.cardBorder,
                        ),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Done", color = colors.primary)
            }
        },
        containerColor = colors.card,
    )
}

@Composable
private fun ColorPresetChip(
    label: String,
    swatch: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val shape = RoundedCornerShape(999.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(shape)
            .background(if (selected) colors.primary.copy(alpha = 0.16f) else colors.chipUnselected)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) colors.primary else colors.cardBorder,
                shape = shape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(Color(swatch or 0xFF000000.toInt()))
                .border(1.dp, colors.textMuted, CircleShape),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = typography.chip,
            color = if (selected) colors.primary else colors.chipUnselectedText,
        )
    }
}

@Composable
private fun HexField(value: String, onValueChange: (String) -> Unit) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val shape = RoundedCornerShape(10.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(shape)
            .background(colors.chipUnselected)
            .border(1.dp, colors.cardBorder, shape)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Text(text = "#", style = typography.chip, color = colors.textMuted)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = typography.chip.copy(color = colors.textPrimary),
            cursorBrush = SolidColor(colors.primary),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
            modifier = Modifier.width(84.dp),
        )
    }
}

@Composable
private fun SliderRow(label: String, content: @Composable () -> Unit) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = label, style = typography.tag, color = colors.textMuted)
        Box(modifier = Modifier.padding(horizontal = 2.dp)) {
            content()
        }
    }
}

private fun sameRgb(a: Int, b: Int): Boolean = (a and 0xFFFFFF) == (b and 0xFFFFFF)

private fun toHex(color: Int): String = "%06X".format(color and 0xFFFFFF)

private val HUE_STOPS = listOf(
    Color(0xFFFF0000),
    Color(0xFFFFFF00),
    Color(0xFF00FF00),
    Color(0xFF00FFFF),
    Color(0xFF0000FF),
    Color(0xFFFF00FF),
    Color(0xFFFF0000),
)
