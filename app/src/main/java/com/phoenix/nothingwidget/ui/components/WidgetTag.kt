package com.phoenix.nothingwidget.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.phoenix.nothingwidget.ui.theme.AppTheme

@Composable
fun WidgetTag(
    label: String,
    modifier: Modifier = Modifier,
    accent: Boolean = false,
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val background = if (accent) {
        colors.primary.copy(alpha = if (colors.isDark) 0.22f else 0.12f)
    } else {
        colors.surfaceVariant
    }
    val content = if (accent) colors.textPrimary else colors.textSecondary

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .padding(horizontal = 10.dp, vertical = 5.dp),
    ) {
        Text(
            text = label,
            style = typography.tag,
            color = content,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WidgetTagRow(
    tags: List<String>,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        tags.forEach { tag ->
            WidgetTag(
                label = tag,
                accent = tag == "New",
            )
        }
    }
}
