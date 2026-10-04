package com.phoenix.nothingwidget.core.widget_config

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

object WidgetCustomizationRepository {

    fun configFlow(context: Context, widget: WidgetCustomization): Flow<WidgetCustomizationConfig> =
        WidgetCustomizationStore.flow(context, widget.defaults)

    /** Blocking read for AppWidgetProvider render paths (single small DataStore read). */
    fun config(context: Context, widget: WidgetCustomization): WidgetCustomizationConfig =
        runBlocking { configFlow(context, widget).first() }

    suspend fun apply(
        context: Context,
        widget: WidgetCustomization,
        config: WidgetCustomizationConfig,
    ) {
        val appContext = context.applicationContext
        withContext(Dispatchers.IO) {
            WidgetCustomizationStore.save(appContext, config)
            widget.refresh(appContext)
        }
    }

    suspend fun reset(context: Context, widget: WidgetCustomization) {
        val appContext = context.applicationContext
        withContext(Dispatchers.IO) {
            WidgetCustomizationStore.clear(appContext, widget.defaults)
            widget.refresh(appContext)
        }
    }
}
