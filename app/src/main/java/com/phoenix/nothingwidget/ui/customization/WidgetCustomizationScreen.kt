package com.phoenix.nothingwidget.ui.customization

import android.widget.Toast
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.phoenix.nothingwidget.core.widget_config.ColorChoice
import com.phoenix.nothingwidget.core.widget_config.ElementProperty
import com.phoenix.nothingwidget.core.widget_config.ElementSpec
import com.phoenix.nothingwidget.core.widget_config.ElementStyleConfig
import com.phoenix.nothingwidget.core.widget_config.StyleChoice
import com.phoenix.nothingwidget.core.widget_config.StylePresets
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomization
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomizationConfig
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomizationRepository
import com.phoenix.nothingwidget.ui.components.PrimaryActionButton
import com.phoenix.nothingwidget.ui.components.SecondaryActionButton
import com.phoenix.nothingwidget.core.widget_config.isSelected
import com.phoenix.nothingwidget.core.widget_config.isTransparent
import com.phoenix.nothingwidget.core.widget_config.with
import com.phoenix.nothingwidget.ui.theme.AppTheme
import kotlinx.coroutines.launch

/** Shared screen: preview → background → per-element options (colors live here only). */
@Composable
fun WidgetCustomizationScreen(
    widgetName: String,
    @Suppress("UNUSED_PARAMETER") previewCircular: Boolean,
    customization: WidgetCustomization,
    onBack: () -> Unit,
    onApplied: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val dimensions = AppTheme.dimensions

    val savedFlow = remember(customization) {
        WidgetCustomizationRepository.configFlow(context, customization)
    }
    val saved by savedFlow.collectAsState(initial = null)
    var draft by remember(customization) { mutableStateOf<WidgetCustomizationConfig?>(null) }
    LaunchedEffect(saved) {
        if (draft == null) draft = saved
    }
    val config = draft ?: return

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensions.medium),
    ) {
        Text(
            text = "‹ Back",
            style = typography.subtitle,
            color = colors.primary,
            modifier = Modifier
                .padding(bottom = dimensions.small)
                .clickable(onClick = onBack),
        )
        Text(
            text = "Customize $widgetName",
            style = typography.title,
            color = colors.textPrimary,
        )

        WidgetCustomizationPreview(
            customization = customization,
            config = config,
        )

        ChoiceSection(
            title = "Background",
            choices = customization.backgroundChoices,
            selected = { choice ->
                choice is ColorChoice && choice.color == config.backgroundColor
            },
            onSelect = { choice ->
                if (choice is ColorChoice) draft = config.withBackground(choice.color)
            },
        )

        customization.elementSpecs.forEach { spec ->
            val element = config.element(spec.elementId)
            ElementSectionCard(
                spec = spec,
                style = element,
                onChange = { property, choice ->
                    draft = config.withElement(element.with(property, choice))
                },
                onColorChange = { argb ->
                    draft = config.withElement(element.copy(textColor = argb))
                },
            )
        }

        PrimaryActionButton(
            text = "Apply",
            enabled = config != saved,
            onClick = {
                scope.launch {
                    WidgetCustomizationRepository.apply(context, customization, config)
                    Toast.makeText(context, "$widgetName updated", Toast.LENGTH_SHORT).show()
                    onApplied?.invoke()
                }
            },
        )

        SecondaryActionButton(
            text = "Reset to default",
            accent = false,
            onClick = {
                scope.launch {
                    WidgetCustomizationRepository.reset(context, customization)
                    draft = customization.defaults
                }
            },
        )
    }
}

@Composable
private fun ElementSectionCard(
    spec: ElementSpec,
    style: ElementStyleConfig,
    onChange: (ElementProperty, StyleChoice) -> Unit,
    onColorChange: (Int) -> Unit,
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val shape = RoundedCornerShape(20.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.card)
            .border(1.dp, colors.cardBorder, shape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = spec.title, style = typography.cardTitle, color = colors.textPrimary)

        val fontsVisible = spec.fontChoices.withoutDefaultLabel()
        val sizesVisible = spec.sizeChoices.withoutDefaultLabel()
        val togglesVisible = spec.toggleChoices.withoutDefaultLabel()
        val iconStylesVisible = spec.iconStyleChoices.withoutDefaultLabel()

        if (ElementProperty.COLOR in spec.properties) {
            PropertyRow("Color") {
                ElementColorPicker(
                    color = style.textColor,
                    presets = spec.colorChoices.ifEmpty { StylePresets.textColors },
                    onColorChange = onColorChange,
                )
            }
        }
        if (ElementProperty.FONT in spec.properties && fontsVisible.isNotEmpty()) {
            PropertyRow("Font") {
                ChoiceChips(
                    choices = fontsVisible,
                    selected = { style.isSelected(ElementProperty.FONT, it) },
                    onSelect = { onChange(ElementProperty.FONT, it) },
                )
            }
        }
        if (ElementProperty.SIZE in spec.properties && sizesVisible.isNotEmpty()) {
            PropertyRow("Size") {
                ChoiceChips(
                    choices = sizesVisible,
                    selected = { style.isSelected(ElementProperty.SIZE, it) },
                    onSelect = { onChange(ElementProperty.SIZE, it) },
                )
            }
        }
        if (ElementProperty.ENABLED in spec.properties && togglesVisible.isNotEmpty()) {
            PropertyRow("Enable") {
                ChoiceChips(
                    choices = togglesVisible,
                    selected = { style.isSelected(ElementProperty.ENABLED, it) },
                    onSelect = { onChange(ElementProperty.ENABLED, it) },
                )
            }
        }
        if (ElementProperty.ICON_STYLE in spec.properties && iconStylesVisible.isNotEmpty()) {
            PropertyRow("Style") {
                ChoiceChips(
                    choices = iconStylesVisible,
                    selected = { style.isSelected(ElementProperty.ICON_STYLE, it) },
                    onSelect = { onChange(ElementProperty.ICON_STYLE, it) },
                )
            }
        }
    }
}

/** Hide "Default" chips — unset / built-in values stay internal. */
private fun List<StyleChoice>.withoutDefaultLabel(): List<StyleChoice> =
    filterNot { it.label.equals("Default", ignoreCase = true) }

@Composable
private fun PropertyRow(label: String, content: @Composable () -> Unit) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = label, style = typography.subtitle, color = colors.textSecondary)
        content()
    }
}

@Composable
private fun ChoiceSection(
    title: String,
    choices: List<StyleChoice>,
    selected: (StyleChoice) -> Boolean,
    onSelect: (StyleChoice) -> Unit,
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val shape = RoundedCornerShape(20.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.card)
            .border(1.dp, colors.cardBorder, shape)
            .padding(16.dp),
    ) {
        Text(text = title, style = typography.cardTitle, color = colors.textPrimary)
        Spacer(modifier = Modifier.height(12.dp))
        ChoiceChips(choices = choices, selected = selected, onSelect = onSelect)
    }
}

@Composable
private fun ChoiceChips(
    choices: List<StyleChoice>,
    selected: (StyleChoice) -> Boolean,
    onSelect: (StyleChoice) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        choices.forEach { choice ->
            StyleChip(
                choice = choice,
                selected = selected(choice),
                onClick = { onSelect(choice) },
            )
        }
    }
}

@Composable
private fun StyleChip(
    choice: StyleChoice,
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
        if (choice is ColorChoice) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(if (isTransparent(choice.color)) Color.Transparent else Color(choice.color))
                    .border(1.dp, colors.textMuted, CircleShape),
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = choice.label,
            style = typography.chip,
            color = if (selected) colors.primary else colors.chipUnselectedText,
        )
    }
}
