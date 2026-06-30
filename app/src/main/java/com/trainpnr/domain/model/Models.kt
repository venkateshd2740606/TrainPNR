package com.trainpnr.domain.model

enum class AppTheme(val displayName: String) {
    SYSTEM("System"), LIGHT("Light"), DARK("Dark")
}

data class UserPreferences(
    val appTheme: AppTheme = AppTheme.SYSTEM,
    val adsEnabled: Boolean = true,
    val consentGiven: Boolean = true,
    val analyticsEnabled: Boolean = true,
    val personalizedAds: Boolean = false,
    val language: String = "system"
)

data class SavedPnr(
    val pnr: String,
    val nickname: String,
    val savedAt: Long = System.currentTimeMillis(),
    val lastStatusText: String? = null
)

data class PnrStatusGuide(
    val code: String,
    val title: String,
    val description: String
)
