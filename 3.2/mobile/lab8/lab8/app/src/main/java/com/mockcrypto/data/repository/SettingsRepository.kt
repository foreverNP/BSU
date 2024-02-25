package com.mockcrypto.data.repository

import com.mockcrypto.domain.model.ThemeMode
import com.mockcrypto.domain.model.UserSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface SettingsRepository {
    val userSettings: StateFlow<UserSettings>
    
    suspend fun getUserSettings(): Result<UserSettings>
    suspend fun updateThemeMode(themeMode: ThemeMode): Result<UserSettings>
    suspend fun updateNotificationsEnabled(enabled: Boolean): Result<UserSettings>
    suspend fun updateLanguage(language: String): Result<UserSettings>
    suspend fun resetAllSettings(): Result<UserSettings>
}

// Mock implementation
class MockSettingsRepository : SettingsRepository {
    // Default settings
    private val _userSettings = MutableStateFlow(UserSettings())
    override val userSettings = _userSettings.asStateFlow()

    override suspend fun getUserSettings(): Result<UserSettings> {
        delay(300) // Simulate network delay
        return Result.success(_userSettings.value)
    }

    override suspend fun updateThemeMode(themeMode: ThemeMode): Result<UserSettings> {
        delay(300) // Simulate network delay
        val updated = _userSettings.value.copy(themeMode = themeMode)
        _userSettings.value = updated
        return Result.success(updated)
    }

    override suspend fun updateNotificationsEnabled(enabled: Boolean): Result<UserSettings> {
        delay(300) // Simulate network delay
        val updated = _userSettings.value.copy(notificationsEnabled = enabled)
        _userSettings.value = updated
        return Result.success(updated)
    }

    override suspend fun updateLanguage(language: String): Result<UserSettings> {
        delay(300) // Simulate network delay
        val updated = _userSettings.value.copy(language = language)
        _userSettings.value = updated
        return Result.success(updated)
    }

    override suspend fun resetAllSettings(): Result<UserSettings> {
        delay(500) // Simulate network delay
        val defaultSettings = UserSettings()
        _userSettings.value = defaultSettings
        return Result.success(defaultSettings)
    }
} 