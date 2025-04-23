package com.example.tfgonitime.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "settings"
val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

object ThemePreferenceManager {
    private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme_enabled")

    fun getThemePreference(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { it[DARK_THEME_KEY] ?: false }
    }

    suspend fun setThemePreference(context: Context, isDark: Boolean) {
        context.dataStore.edit { it[DARK_THEME_KEY] = isDark }
    }
}
