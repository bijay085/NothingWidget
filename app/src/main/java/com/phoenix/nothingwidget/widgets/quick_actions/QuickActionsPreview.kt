package com.phoenix.nothingwidget.widgets.quick_actions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomizationConfig

/**
 * Compose-only Customize preview for Quick Actions.
 * Reads draft [config] — never touches AppWidgetManager / pin / RemoteViews host.
 */
@Composable
fun QuickActionsPreview(
    config: WidgetCustomizationConfig,
    modifier: Modifier = Modifier,
) {
    val actions = remember(config) {
        QuickActionsSettings.resolveVisible(config)
    }
    val background = Color(config.backgroundColor)
    val contentColor = Color.White

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .padding(horizontal = 10.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            actions.forEach { action ->
                QuickActionsPreviewSlot(
                    action = action,
                    contentColor = contentColor,
                    modifier = Modifier.widthIn(min = 72.dp),
                )
            }
        }
    }
}

@Composable
private fun QuickActionsPreviewSlot(
    action: QuickActionType,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Image(
            painter = painterResource(action.iconRes),
            contentDescription = action.choiceLabel,
            modifier = Modifier.size(26.dp),
            colorFilter = ColorFilter.tint(contentColor),
        )
        Text(
            text = action.choiceLabel,
            color = contentColor,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
