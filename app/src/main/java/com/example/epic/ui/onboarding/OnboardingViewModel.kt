package com.example.epic.ui.onboarding

import com.example.epic.sharedPrefs.onboardingPrefs.OnboardingPreferences
import com.example.epic.ui.base.viewmodel.BaseViewModel

class OnboardingViewModel(onboardingPreferences: OnboardingPreferences) : BaseViewModel() {

    init {
        onboardingPreferences.isOnboardingShown = true
    }
}