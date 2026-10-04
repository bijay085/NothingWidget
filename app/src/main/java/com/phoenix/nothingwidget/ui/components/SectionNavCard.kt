package com.phoenix.nothingwidget.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.phoenix.nothingwidget.ui.theme.AppTheme

data class SectionEntry(
    val title: String,
    val subtitle: String,
    val muted: Boolean,
    val onClick: () -> Unit,
)

/** One collapsible card grouping the special destinations; rows are hidden until expanded. */
@Composable
fun SectionGroupCard(
    title: String,
    summary: String,
    entries: List<SectionEntry>,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val dimensions = AppTheme.dimensions
    val shape = RoundedCornerShape(dimensions.cornerRadius)
    var expanded by rememberSaveable { mutableStateOf(false) }
    val arrowRotation by animateFloatAsState(if (expanded) 90f else 0f, label = "sectionArrow")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (colors.isDark) 4.dp else 6.dp,
                shape = shape,
                ambientColor = colors.shadow.copy(alpha = if (colors.isDark) 0.20f else 0.08f),
                spotColor = colors.shadow.copy(alpha = if (colors.isDark) 0.26f else 0.12f),
            )
            .clip(shape)
            .background(colors.card)
            .cardDecoration()
            .border(1.dp, colors.cardBorder, shape),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(dimensions.medium),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = typography.section, color = colors.textPrimary)
                Spacer(modifier = Modifier.height(dimensions.small / 2))
                Text(text = summary, style = typography.subtitle, color = colors.textSecondary)
            }
            ChevronBadge(modifier = Modifier.rotate(arrowRotation))
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            Column {
                entries.forEach { entry ->
                    HorizontalDivider(color = colors.divider)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = entry.onClick)
                            .padding(horizontal = dimensions.medium, vertical = 14.dp),
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = entry.title, style = typography.cardTitle, color = colors.textPrimary)
                            Text(
                                text = entry.subtitle,
                                style = typography.subtitle,
                                color = if (entry.muted) colors.textMuted else colors.textSecondary,
                            )
                        }
                        ChevronBadge()
                    }
                }
            }
        }
    }
}
