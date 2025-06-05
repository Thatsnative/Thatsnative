package com.example.epic.ui.tabFragment

import androidx.lifecycle.viewModelScope
import com.example.epic.R
import com.example.epic.coroutine.SingleFlowEvent
import com.example.epic.sharedPrefs.authPrefs.AuthPreferences
import com.example.epic.sharedPrefs.onboardingPrefs.OnboardingPreferences
import com.example.epic.ui.base.viewmodel.BaseViewModel

class TabViewModel(
    private val authPreferences: AuthPreferences,
    private val onboardingPreferences: OnboardingPreferences
) : BaseViewModel() {
    private val _navigateToDestination = SingleFlowEvent<Int?>()
    val navigateToDestination = _navigateToDestination.asFlow()

    init {
        val destination = when {
//            !onboardingPreferences.isOnboardingShown -> R.id.action_tabFragment_to_onboardingFragment
//            authPreferences.getToken().isEmpty() -> R.id.action_tabFragment_to_authFragment
            else -> null
        }
        _navigateToDestination.tryEmitScope(
            viewModelScope,
            destination
        )
    }
}