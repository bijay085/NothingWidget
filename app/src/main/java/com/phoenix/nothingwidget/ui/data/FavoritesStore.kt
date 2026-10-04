package com.phoenix.nothingwidget.ui.data

import android.content.Context
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val LEGACY_PREFS_NAME = "nothing_widget_shell"
private const val KEY_FAVORITES = "favorite_ids"

private val Context.favoritesDataStore by preferencesDataStore(
    name = "nothing_widget_favorites",
    produceMigrations = { context ->
        listOf(SharedPreferencesMigration(context, LEGACY_PREFS_NAME, setOf(KEY_FAVORITES)))
    },
)

object FavoritesStore {
    private val favoritesKey = stringSetPreferencesKey(KEY_FAVORITES)

    fun favoritesFlow(context: Context): Flow<Set<String>> =
        context.applicationContext.favoritesDataStore.data.map { prefs ->
            prefs[favoritesKey].orEmpty()
        }

    suspend fun toggle(context: Context, id: String) {
        context.applicationContext.favoritesDataStore.edit { prefs ->
            val current = prefs[favoritesKey].orEmpty()
            prefs[favoritesKey] = if (id in current) current - id else current + id
        }
    }
}
