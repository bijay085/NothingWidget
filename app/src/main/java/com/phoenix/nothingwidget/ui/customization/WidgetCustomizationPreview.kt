package com.phoenix.nothingwidget.ui.customization

import android.appwidget.AppWidgetHostView
import android.view.View
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomization
import com.phoenix.nothingwidget.core.widget_config.WidgetCustomizationConfig
import com.phoenix.nothingwidget.ui.theme.AppTheme
import com.phoenix.nothingwidget.widgets.quick_actions.QuickActionsConfig
import com.phoenix.nothingwidget.widgets.quick_actions.QuickActionsPreview

/**
 * Live Customize-screen preview.
 *
 * Quick Actions uses a Compose-only strip ([QuickActionsPreview]) — AppWidgetHostView
 * fails on its HorizontalScrollView layout ("Couldn't add widget").
 * Other widgets still host RemoteViews at a home-screen-like cell size.
 */
@Composable
fun WidgetCustomizationPreview(
    customization: WidgetCustomization,
    config: WidgetCustomizationConfig,
) {
    val colors = AppTheme.colors
    val shape = RoundedCornerShape(20.dp)
    val density = LocalDensity.current
    val hostPx = remember(density) { with(density) { PREVIEW_HOST_SIZE.roundToPx() } }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(PREVIEW_AREA_HEIGHT)
            .clip(shape)
            .background(colors.previewSurface)
            .border(1.dp, colors.previewBorder, shape),
    ) {
        if (customization.widgetId == QuickActionsConfig.WIDGET_ID) {
            QuickActionsPreview(
                config = config,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )
        } else {
            AndroidView(
                factory = { viewContext ->
                    AppWidgetHostView(viewContext).apply {
                        setPadding(0, 0, 0, 0)
                        layoutParams = FrameLayout.LayoutParams(hostPx, hostPx)
                    }
                },
                update = { host ->
                    // Reset any leftover scale from older preview builds.
                    host.scaleX = 1f
                    host.scaleY = 1f
                    host.pivotX = 0f
                    host.pivotY = 0f

                    val lp = host.layoutParams ?: FrameLayout.LayoutParams(hostPx, hostPx)
                    if (lp.width != hostPx || lp.height != hostPx) {
                        lp.width = hostPx
                        lp.height = hostPx
                        host.layoutParams = lp
                    }

                    host.updateAppWidget(customization.previewViews(host.context, config))
                    host.measure(
                        View.MeasureSpec.makeMeasureSpec(hostPx, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(hostPx, View.MeasureSpec.EXACTLY),
                    )
                    host.layout(0, 0, hostPx, hostPx)
                },
                modifier = Modifier.size(PREVIEW_HOST_SIZE),
            )
        }
    }
}

/** Fixed preview card. */
private val PREVIEW_AREA_HEIGHT = 220.dp

/**
 * Host size ≈ typical launcher 2×2 cell.
 * Large enough to read; not so large that edge-aligned elements look split.
 * No scaleX/scaleY — those glitch RemoteViews font variants.
 */
private val PREVIEW_HOST_SIZE = 156.dp
