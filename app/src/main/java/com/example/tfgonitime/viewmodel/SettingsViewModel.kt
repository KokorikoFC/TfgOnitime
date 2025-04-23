package com.example.tfgonitime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.tfgonitime.data.repository.ThemePreferenceManager

class SettingsViewModel(private val context: android.content.Context) : ViewModel() {

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> get() = _isDarkTheme

    init {
        // Carga la preferencia guardada
        viewModelScope.launch {
            ThemePreferenceManager.getThemePreference(context).collect {
                _isDarkTheme.value = it
            }
        }
    }

    fun toggleDarkTheme(enabled: Boolean) {
        _isDarkTheme.value = enabled
        viewModelScope.launch {
            ThemePreferenceManager.setThemePreference(context, enabled)
        }
    }
}
