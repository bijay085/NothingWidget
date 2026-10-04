package com.phoenix.nothingwidget.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.phoenix.nothingwidget.ui.theme.AppTheme

/**
 * Navigation-only card for special destinations.
 * Never embeds widget cards.
 */
@Composable
fun SectionNavCard(
    title: String,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    emptyLabel: String = "No items",
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val dimensions = AppTheme.dimensions
    val shape = RoundedCornerShape(dimensions.cornerRadius)
    val countLabel = when (count) {
        0 -> emptyLabel
        1 -> "1 widget"
        else -> "$count widgets"
    }
    val elevation = if (colors.isDark) 4.dp else 6.dp

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = colors.shadow.copy(alpha = if (colors.isDark) 0.20f else 0.08f),
                spotColor = colors.shadow.copy(alpha = if (colors.isDark) 0.26f else 0.12f),
            )
            .clip(shape)
            .background(colors.card)
            .border(1.dp, colors.cardBorder, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = dimensions.medium, vertical = dimensions.medium),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = typography.section,
                color = colors.textPrimary,
            )
            Spacer(modifier = Modifier.height(dimensions.small / 2))
            Text(
                text = countLabel,
                style = typography.subtitle,
                color = if (count == 0) colors.textMuted else colors.textSecondary,
            )
        }
        Text(
            text = "›",
            style = typography.section,
            color = colors.textMuted,
        )
    }
}
