package com.mockcrypto.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.mockcrypto.data.repository.MockSettingsRepository
import com.mockcrypto.domain.model.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ThemeController {
    // Default to using system settings
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()
    
    // For initial app setup, synchronize with repository
    private val settingsRepository = MockSettingsRepository()
    
    init {
        // In a real app, we'd use coroutines to load this properly
        // For simplicity in the mock, we'll just use the default
    }
    
    fun updateThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }
    
    @Composable
    fun isDarkTheme(): Boolean {
        val themeMode by themeMode.collectAsState()
        return when (themeMode) {
            ThemeMode.DARK -> true
            ThemeMode.LIGHT -> false
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
        }
    }
} 