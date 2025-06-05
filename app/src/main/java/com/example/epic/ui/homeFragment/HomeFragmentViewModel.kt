package com.example.epic.ui.homeFragment

import androidx.lifecycle.viewModelScope
import com.example.epic.coroutine.SingleFlowEvent
import com.example.epic.sharedPrefs.mainFeaturesPrefs.MainFeaturesPreferences
import com.example.epic.ui.base.viewmodel.BaseViewModel
import com.example.epic.ui.homeFragment.adapter.HomeFeatureItemModel
import com.example.epic.ui.homeFragment.adapter.getEnhanceYourProtectionItems
import com.example.epic.ui.homeFragment.adapter.getExploreFeaturesItems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeFragmentViewModel(
    private val mainFeaturesPreferences: MainFeaturesPreferences
) : BaseViewModel() {
    private val _toggleVpnButton = SingleFlowEvent<Unit>()
    val toggleVpnButton = _toggleVpnButton.asFlow()

    private val _isRunning = MutableStateFlow(mainFeaturesPreferences.isVPNRunning)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val _isNeedHelpVisible = MutableStateFlow(false)
    val isNeedHelpVisible: StateFlow<Boolean> = _isNeedHelpVisible

    private val _adBlockCount = MutableStateFlow(mainFeaturesPreferences.adBlockCount)
    val adBlockCount: StateFlow<Int> = _adBlockCount

    private val _enhanceYourProtectionList =
        SingleFlowEvent<List<HomeFeatureItemModel>>()
    val enhanceYourProtectionList = _enhanceYourProtectionList.asFlow()

    private val _exploreFeaturesList =
        SingleFlowEvent<List<HomeFeatureItemModel>>()
    val exploreFeaturesList = _exploreFeaturesList.asFlow()

    init {
        mainFeaturesPreferences.registerIsVpnRunningListener {
            _isRunning.value = it
        }
        mainFeaturesPreferences.registerAdBlockCountListener {
            _adBlockCount.value = it
        }
        updateProtectionsState()
    }

    fun onEnhanceYourProtectionSwitchClick(item: HomeFeatureItemModel) {
        when (item.id) {
            0 -> mainFeaturesPreferences.isYoutubeProtectionEnabled = item.isChecked
            1 -> mainFeaturesPreferences.isSpeedChargeEnabled = item.isChecked
            2 -> mainFeaturesPreferences.isPrivacyProtectionEnabled = item.isChecked
            3 -> mainFeaturesPreferences.isMalwareProtectionEnabled = item.isChecked
        }
    }

    fun onExploreFeaturesSwitchClick(item: HomeFeatureItemModel) {
        when (item.id) {
            0 -> mainFeaturesPreferences.isCookieProtectionEnabled = item.isChecked
            1 -> mainFeaturesPreferences.isSocialMediaProtectionEnabled = item.isChecked
        }
    }

    fun onToggleButtonClick() {
        _toggleVpnButton.tryEmitScope(viewModelScope, Unit)
    }

    override fun onCleared() {
        super.onCleared()
        mainFeaturesPreferences.unregisterIsVpnRunningListener()
    }

    fun isAllProtectionsEnabled() = mainFeaturesPreferences.run {
        isYoutubeProtectionEnabled && isPrivacyProtectionEnabled && isMalwareProtectionEnabled &&
                isSocialMediaProtectionEnabled && isCookieProtectionEnabled
    }

    fun updateAllProtections(enabled: Boolean = true) {
        mainFeaturesPreferences.run {
            isYoutubeProtectionEnabled = enabled
            isSpeedChargeEnabled = enabled
            isPrivacyProtectionEnabled = enabled
            isMalwareProtectionEnabled = enabled
            isSocialMediaProtectionEnabled = enabled
            isCookieProtectionEnabled = enabled
        }
        updateProtectionsState()
    }

    fun updateProtectionsState() {
        _exploreFeaturesList.tryEmitScope(
            viewModelScope,
            getExploreFeaturesItems(mainFeaturesPreferences)
        )
        _enhanceYourProtectionList.tryEmitScope(
            viewModelScope,
            getEnhanceYourProtectionItems(mainFeaturesPreferences)
        )
    }

    fun updateNeedHelpState(allPermissionsGranted: Boolean) {
        _isNeedHelpVisible.value = !allPermissionsGranted
    }

    override fun onDetach() {
        mainFeaturesPreferences.unregisterIsVpnRunningListener()
        mainFeaturesPreferences.unregisterAdBlockCountListener()
    }
}