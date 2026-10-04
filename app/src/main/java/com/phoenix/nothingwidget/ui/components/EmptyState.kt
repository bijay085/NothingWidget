package com.phoenix.nothingwidget.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.phoenix.nothingwidget.ui.theme.AppTheme

@Composable
fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val dimensions = AppTheme.dimensions

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensions.small),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.large * 2, horizontal = dimensions.medium - dimensions.small / 2),
    ) {
        Text(
            text = title,
            style = typography.cardTitle,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = message,
            style = typography.subtitle,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
        )
    }
}
