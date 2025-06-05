package com.example.epic.sharedPrefs.mainFeaturesPrefs

interface MainFeaturesPreferences {
    var isYoutubeProtectionEnabled: Boolean
    var isSpeedChargeEnabled: Boolean
    var isPrivacyProtectionEnabled: Boolean
    var isCookieProtectionEnabled: Boolean
    var isMalwareProtectionEnabled: Boolean
    var isSocialMediaProtectionEnabled: Boolean

    var isVPNRunning: Boolean
    fun registerIsVpnRunningListener(listener: (Boolean) -> Unit)
    fun unregisterIsVpnRunningListener()

    var adBlockCount: Int
    fun registerAdBlockCountListener(listener: (Int) -> Unit)
    fun unregisterAdBlockCountListener()
}