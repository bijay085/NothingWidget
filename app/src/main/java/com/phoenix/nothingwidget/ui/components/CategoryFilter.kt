package com.phoenix.nothingwidget.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.phoenix.nothingwidget.ui.model.LibraryFilter
import com.phoenix.nothingwidget.ui.theme.AppTheme

@Composable
fun CategoryFilter(
    filters: List<LibraryFilter>,
    selected: LibraryFilter,
    onSelected: (LibraryFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val dimensions = AppTheme.dimensions

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dimensions.filterChipSpacing),
        contentPadding = PaddingValues(horizontal = dimensions.large),
    ) {
        items(filters, key = { it.label }) { filter ->
            val isSelected = filter == selected
            val shape = RoundedCornerShape(999.dp)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .height(dimensions.filterChipHeight)
                    .clip(shape)
                    .background(
                        if (isSelected) colors.primary else colors.chipUnselected,
                    )
                    .then(
                        if (isSelected) {
                            Modifier
                        } else {
                            Modifier.border(1.dp, colors.divider, shape)
                        },
                    )
                    .clickable { onSelected(filter) }
                    .padding(horizontal = dimensions.medium),
            ) {
                Text(
                    text = filter.label,
                    style = typography.chip,
                    color = if (isSelected) colors.onPrimary else colors.chipUnselectedText,
                )
            }
        }
    }
}
