package com.trainpnr.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.trainpnr.domain.model.AppTheme
import com.trainpnr.domain.model.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("trainpnr_prefs")

@Singleton
class PreferencesDataStore @Inject constructor(@ApplicationContext private val context: Context) {
    private object Keys {
        val THEME = stringPreferencesKey("theme")
        val ADS = booleanPreferencesKey("ads")
        val CONSENT = booleanPreferencesKey("consent")
        val ANALYTICS = booleanPreferencesKey("analytics")
        val PERSONALIZED = booleanPreferencesKey("personalized_ads")
        val LANGUAGE = stringPreferencesKey("language")
    }

    val preferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { p ->
        UserPreferences(
            appTheme = runCatching { AppTheme.valueOf(p[Keys.THEME] ?: AppTheme.SYSTEM.name) }.getOrDefault(AppTheme.SYSTEM),
            adsEnabled = p[Keys.ADS] ?: true,
            consentGiven = p[Keys.CONSENT] ?: true,
            analyticsEnabled = p[Keys.ANALYTICS] ?: true,
            personalizedAds = p[Keys.PERSONALIZED] ?: false,
            language = p[Keys.LANGUAGE] ?: "system"
        )
    }

    suspend fun update(transform: (UserPreferences) -> UserPreferences) {
        context.dataStore.edit { prefs ->
            val cur = UserPreferences(
                runCatching { AppTheme.valueOf(prefs[Keys.THEME] ?: AppTheme.SYSTEM.name) }.getOrDefault(AppTheme.SYSTEM),
                prefs[Keys.ADS] ?: true, prefs[Keys.CONSENT] ?: true,
                prefs[Keys.ANALYTICS] ?: true, prefs[Keys.PERSONALIZED] ?: false,
                prefs[Keys.LANGUAGE] ?: "system"
            )
            val u = transform(cur)
            prefs[Keys.THEME] = u.appTheme.name
            prefs[Keys.ADS] = u.adsEnabled
            prefs[Keys.CONSENT] = u.consentGiven
            prefs[Keys.ANALYTICS] = u.analyticsEnabled
            prefs[Keys.PERSONALIZED] = u.personalizedAds
            prefs[Keys.LANGUAGE] = u.language
        }
    }
}
