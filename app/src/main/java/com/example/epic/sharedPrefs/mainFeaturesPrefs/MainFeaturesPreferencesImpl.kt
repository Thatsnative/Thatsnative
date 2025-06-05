package com.example.epic.sharedPrefs.mainFeaturesPrefs

import android.content.SharedPreferences
import com.example.epic.common.DEFAULT_INT
import com.example.epic.sharedPrefs.base.PreferencesManager

const val PREF_IS_MALWARE_PROTECTION_ENABLED = "PREF_IS_MALWARE_PROTECTION_ENABLED"
const val PREF_IS_COOKIE_PROTECTION_ENABLED = "PREF_IS_COOKIE_PROTECTION_ENABLED"
const val PREF_IS_YOUTUBE_PROTECTION_ENABLED = "PREF_IS_YOUTUBE_PROTECTION_ENABLED"
const val PREF_IS_VPN_RUNNING = "PREF_IS_VPN_RUNNING"
const val PREF_AD_BLOCK_COUNT = "PREF_AD_BLOCK_COUNT"
const val PREF_IS_SPEED_CHARGE_ENABLED = "PREF_IS_SPEED_CHARGE_ENABLED"
const val PREF_IS_PRIVACY_PROTECTION_ENABLED = "PREF_IS_PRIVACY_PROTECTION_ENABLED"
const val PREF_IS_SOCIAL_MEDIA_PROTECTION_ENABLED = "PREF_IS_SOCIAL_MEDIA_PROTECTION_ENABLED"

class MainFeaturesPreferencesImpl(private val manager: PreferencesManager) :
    MainFeaturesPreferences {
    private var isVpnRunningListener: SharedPreferences.OnSharedPreferenceChangeListener? = null
    private var adBlockCountListener: SharedPreferences.OnSharedPreferenceChangeListener? = null

    override var isMalwareProtectionEnabled: Boolean
        get() = manager.getBoolean(PREF_IS_MALWARE_PROTECTION_ENABLED, false)
        set(value) {
            manager.put(PREF_IS_MALWARE_PROTECTION_ENABLED, value)
        }

    override var isCookieProtectionEnabled: Boolean
        get() = manager.getBoolean(PREF_IS_COOKIE_PROTECTION_ENABLED, false)
        set(value) {
            manager.put(PREF_IS_COOKIE_PROTECTION_ENABLED, value)
        }

    override var isYoutubeProtectionEnabled: Boolean
        get() = manager.getBoolean(PREF_IS_YOUTUBE_PROTECTION_ENABLED, false)
        set(value) {
            manager.put(PREF_IS_YOUTUBE_PROTECTION_ENABLED, value)
        }

    override var isSpeedChargeEnabled: Boolean
        get() = manager.getBoolean(PREF_IS_SPEED_CHARGE_ENABLED, false)
        set(value) {
            manager.put(PREF_IS_SPEED_CHARGE_ENABLED, value)
        }

    override var isPrivacyProtectionEnabled: Boolean
        get() = manager.getBoolean(PREF_IS_PRIVACY_PROTECTION_ENABLED, false)
        set(value) {
            manager.put(PREF_IS_PRIVACY_PROTECTION_ENABLED, value)
        }

    override var isSocialMediaProtectionEnabled: Boolean
        get() = manager.getBoolean(PREF_IS_SOCIAL_MEDIA_PROTECTION_ENABLED, false)
        set(value) {
            manager.put(PREF_IS_SOCIAL_MEDIA_PROTECTION_ENABLED, value)
        }

    override var isVPNRunning: Boolean
        get() = manager.getBoolean(PREF_IS_VPN_RUNNING, false)
        set(value) {
            manager.put(PREF_IS_VPN_RUNNING, value)
        }

    override var adBlockCount: Int
        get() = manager.getInt(PREF_AD_BLOCK_COUNT, DEFAULT_INT)
        set(value) {
            manager.put(PREF_AD_BLOCK_COUNT, value)
        }

    override fun registerIsVpnRunningListener(listener: (Boolean) -> Unit) {
        unregisterIsVpnRunningListener()
        isVpnRunningListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == PREF_IS_VPN_RUNNING) {
                listener.invoke(isVPNRunning)
            }
        }
        isVpnRunningListener?.let { manager.registerListener(it) }
    }

    override fun unregisterIsVpnRunningListener() {
        isVpnRunningListener?.let { manager.unregisterListener(it) }
    }

    override fun registerAdBlockCountListener(listener: (Int) -> Unit) {
        unregisterAdBlockCountListener()
        adBlockCountListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == PREF_AD_BLOCK_COUNT) {
                listener.invoke(adBlockCount)
            }
        }
        adBlockCountListener?.let { manager.registerListener(it) }
    }

    override fun unregisterAdBlockCountListener() {
        adBlockCountListener?.let { manager.unregisterListener(it) }
    }
}