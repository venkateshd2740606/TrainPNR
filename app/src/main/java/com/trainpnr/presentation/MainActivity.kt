package com.trainpnr.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.trainpnr.ads.AdManager
import com.trainpnr.analytics.AnalyticsManager
import com.trainpnr.domain.model.UserPreferences
import com.trainpnr.domain.repository.PreferencesRepository
import com.trainpnr.presentation.navigation.TrainPNRNavHost
import com.trainpnr.presentation.ui.theme.TrainPNRTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject lateinit var adManager: AdManager
    @Inject lateinit var analyticsManager: AnalyticsManager
    @Inject lateinit var preferencesRepository: PreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        adManager.initialize()
        setContent {
            val prefs by preferencesRepository.getUserPreferences().collectAsStateWithLifecycle(initialValue = null)
            if (prefs == null) {
                TrainPNRTheme { Box(Modifier.fillMaxSize()) }
                return@setContent
            }
            TrainPNRRoot(prefs!!, adManager, analyticsManager)
        }
    }
}

@Composable
private fun TrainPNRRoot(prefs: UserPreferences, adManager: AdManager, analyticsManager: AnalyticsManager) {
    LaunchedEffect(prefs.analyticsEnabled) { analyticsManager.setCollectionEnabled(prefs.analyticsEnabled) }
    LaunchedEffect(prefs.adsEnabled, prefs.personalizedAds) { adManager.updateAdPolicy(prefs.adsEnabled, prefs.personalizedAds) }
    TrainPNRTheme(prefs.appTheme) {
        TrainPNRNavHost(rememberNavController(), adManager, analyticsManager, prefs)
    }
}
