package com.example.epic.ui.main

import androidx.lifecycle.ViewModel
import com.example.epic.sharedPrefs.mainFeaturesPrefs.MainFeaturesPreferences
import com.example.epic.sharedPrefs.permissionPrefs.PermissionsPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel(
    private val mainFeaturesPreferences: MainFeaturesPreferences,
    private val permissionsPreferences: PermissionsPreferences
    ) : ViewModel() {
    private val _isSplashShown = MutableStateFlow(false)
    val isSplashShown: StateFlow<Boolean> = _isSplashShown

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isApplied = MutableStateFlow(false)
    val isApplied: StateFlow<Boolean> = _isApplied

    fun setIsLoading(value: Boolean) {
        _isLoading.value = value
    }

    fun setIsApplied(value: Boolean) {
        _isApplied.value = value
    }

    fun setIsSplashShown(value: Boolean) {
        _isSplashShown.value = value

    }

    fun runVpn() {
        mainFeaturesPreferences.isVPNRunning = true
    }

    fun stopVpn() {
        mainFeaturesPreferences.isVPNRunning = false
    }

    fun incrementNotificationRequestCount() {
        permissionsPreferences.notificationRequestCount++
    }
}