package com.example.tfgonitime.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Locale

class LanguageViewModel : ViewModel() {
    private val _locale = mutableStateOf(Locale.getDefault())
    val locale: State<Locale> get() = _locale

    fun setLocale(locale: Locale) {
        _locale.value = locale
    }
}

