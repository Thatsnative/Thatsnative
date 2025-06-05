package com.example.epic.sharedPrefs.onboardingPrefs

import com.example.epic.sharedPrefs.base.PreferencesManager

private const val PREF_ONBOARDING_SHOWN = "PREF_ONBOARDING_SHOWN"

class OnboardingPreferencesImpl(private val manager: PreferencesManager) : OnboardingPreferences {
    override var isOnboardingShown: Boolean
        get() = manager.getBoolean(PREF_ONBOARDING_SHOWN)
        set(value) {
            manager.put(PREF_ONBOARDING_SHOWN, value)
        }
}