package com.example.tfgonitime.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgonitime.R // Importa tu archivo R si usas un drawable por defecto
import com.example.tfgonitime.data.repository.ThemePreferenceManager // Puedes mantener esto o migrar toda la lógica de tema a DataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey // Para el tema oscuro, si lo guardas aquí
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged // Para optimizar las actualizaciones del flow
import com.example.tfgonitime.data.settingsDataStore

// --- Mueve la importación de DataStore aquí ---
// Necesitas una forma de acceder a tu instancia de DataStore.
// Aquí asumimos que tienes una extensión definida en tu proyecto, por ejemplo:
// val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_preferences")
// Si no tienes esta extensión, deberás crearla o ajustar cómo obtienes la instancia de DataStore.
import com.example.tfgonitime.data.settingsDataStore // <--- ¡Esta línea debe estar aquí arriba!


// --- Definición de las claves de DataStore ---
// Coloca estas claves en un lugar accesible, como un archivo de constantes o con la extensión de Context para DataStore.
// Aquí las definimos para el ejemplo, asumiendo que tu DataStore gestiona estas preferencias.
val PROFILE_PICTURE_RESOURCE_ID_KEY = intPreferencesKey("profile_picture_resource_id")
val IS_DARK_THEME_KEY = booleanPreferencesKey("is_dark_theme") // Clave para el tema oscuro


class SettingsViewModel(private val context: Context) : ViewModel() {
    // No necesitamos AuthViewModel como parámetro si solo gestionamos el estado local

    // --- Gestión del Tema Oscuro ---
    // Este estado local en el ViewModel representa el valor actual del tema
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> get() = _isDarkTheme

    // --- Gestión de la Foto de Perfil ---
    // **Mantenemos el StateFlow local para la foto de perfil**
    // Estado para la ID del recurso drawable de la foto seleccionada
    // Se inicializa con una imagen por defecto (ajústala según tus drawables)
    private val _profilePictureResource = MutableStateFlow(R.drawable.head_onigiri)
    val profilePictureResource: StateFlow<Int> = _profilePictureResource.asStateFlow()


    // Obtenemos la instancia de DataStore a través de la extensión de Context
    private val dataStore: DataStore<Preferences> = context.settingsDataStore

    init {
        // --- Cargar preferencias guardadas al iniciar el ViewModel ---
        viewModelScope.launch {
            // Cargar preferencia de Tema Oscuro
            dataStore.data
                .map { preferences -> preferences[IS_DARK_THEME_KEY] ?: false } // Valor por defecto: false (tema claro)
                .distinctUntilChanged() // Solo emitir si el valor realmente cambia
                .collect { isDark ->
                    _isDarkTheme.value = isDark // Actualiza el StateFlow local
                    Log.d("SettingsViewModel", "Loaded dark theme preference: $isDark")
                }
        }
        viewModelScope.launch {
            // Cargar preferencia de Foto de Perfil
            dataStore.data
                .map { preferences -> preferences[PROFILE_PICTURE_RESOURCE_ID_KEY] ?: R.drawable.head_onigiri } // Valor por defecto: tu drawable predeterminado
                .distinctUntilChanged() // Solo emitir si el valor realmente cambia
                .collect { resourceId ->
                    _profilePictureResource.value = resourceId // Actualiza el StateFlow local
                    Log.d("SettingsViewModel", "Loaded profile picture preference: $resourceId")
                }
        }
    }

    // Función para alternar el tema oscuro y guardarlo en DataStore
    fun toggleDarkTheme(enabled: Boolean) {
        _isDarkTheme.value = enabled // Actualiza el StateFlow local inmediatamente
        viewModelScope.launch {
            dataStore.edit { settings ->
                settings[IS_DARK_THEME_KEY] = enabled // Guarda el nuevo valor en DataStore
            }
            Log.d("SettingsViewModel", "Saved dark theme preference: $enabled")
        }
    }

    // Función para establecer la foto de perfil y guardarla localmente (en DataStore)
    fun setProfilePicture(resourceId: Int) {
        _profilePictureResource.value = resourceId // Actualiza el StateFlow local inmediatamente (la UI reacciona)
        viewModelScope.launch {
            dataStore.edit { settings ->
                settings[PROFILE_PICTURE_RESOURCE_ID_KEY] = resourceId // Guarda la ID del recurso en DataStore
            }
            Log.d("SettingsViewModel", "Saved profile picture preference: $resourceId")
        }
    }

    // ... puedes añadir más funciones para otras configuraciones que quieras guardar localmente ...
}