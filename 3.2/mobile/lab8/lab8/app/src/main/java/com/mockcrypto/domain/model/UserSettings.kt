package com.mockcrypto.domain.model

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

data class UserSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val notificationsEnabled: Boolean = true,
    val language: String = "en" // "en" or "ru"
) 