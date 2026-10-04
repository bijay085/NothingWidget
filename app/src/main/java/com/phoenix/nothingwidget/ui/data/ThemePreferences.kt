package com.phoenix.nothingwidget.ui.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.phoenix.nothingwidget.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "nothing_widget_theme",
)

object ThemePreferences {
    private val KEY_THEME_MODE = stringPreferencesKey("app_theme_mode")

    fun themeModeFlow(context: Context): Flow<AppThemeMode> {
        return context.themeDataStore.data.map { prefs ->
            AppThemeMode.fromStorage(prefs[KEY_THEME_MODE])
        }
    }

    suspend fun setThemeMode(context: Context, mode: AppThemeMode) {
        context.themeDataStore.edit { prefs ->
            prefs[KEY_THEME_MODE] = mode.name
        }
    }
}
