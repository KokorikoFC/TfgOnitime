// DataStorePreferences.kt
package com.example.tfgonitime.data // <--- Asegúrate de que este paquete es correcto

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

// Nombre del archivo donde se guardarán las preferencias (puedes cambiarlo si quieres)
private const val SETTINGS_PREFERENCES_NAME = "settings_preferences"

// Esta es la propiedad de extensión que necesitas definir.
// Permite acceder a tu DataStore usando context.settingsDataStore.
// NO va dentro de una clase.
val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = SETTINGS_PREFERENCES_NAME
)

/*
// Opcional: Puedes definir aquí las claves de preferencias si lo prefieres en lugar de en el ViewModel
// import androidx.datastore.preferences.core.intPreferencesKey
// import androidx.datastore.preferences.core.booleanPreferencesKey
//
// object PreferencesKeys {
//    val PROFILE_PICTURE_RESOURCE_ID = intPreferencesKey("profile_picture_resource_id")
//    val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
// }
*/