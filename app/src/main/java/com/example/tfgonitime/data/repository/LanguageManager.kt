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
        Locale.setDefault(locale)
        currentLocale.value = locale
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        saveLocale(context, locale)
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





