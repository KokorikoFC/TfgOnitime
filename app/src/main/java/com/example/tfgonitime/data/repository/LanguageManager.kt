package com.example.tfgonitime.data.repository

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.mutableStateOf
import java.util.Locale

object LanguageManager {
    private const val PREFS_NAME = "language_prefs"
    private const val PREF_LOCALE = "locale"
    var currentLocale = mutableStateOf(Locale.getDefault())

    fun setLocale(context: Context, locale: Locale) {
        // Solo actualiza si el locale es diferente
        if (locale.language != currentLocale.value.language) {
            Locale.setDefault(locale)
            currentLocale.value = locale
            val config = Configuration(context.resources.configuration)
            config.setLocale(locale)
            val newContext = context.createConfigurationContext(config)
            saveLocale(context, locale)

            // Reiniciar la actividad actual para aplicar los cambios de idioma
            val activity = context as? Activity
            activity?.let {
                it.recreate()
            }
        }
    }

    private fun saveLocale(context: Context, locale: Locale) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(PREF_LOCALE, locale.language)
        editor.apply()
    }

    fun loadLocale(context: Context): Locale {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val language = prefs.getString(PREF_LOCALE, Locale.getDefault().language)
        val locale = Locale(language ?: Locale.getDefault().language)
        setLocale(context, locale)
        return locale
    }
}





