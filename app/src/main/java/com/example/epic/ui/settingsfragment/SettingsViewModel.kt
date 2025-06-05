package com.example.epic.ui.settingsfragment

import androidx.lifecycle.viewModelScope
import com.example.epic.authRepository.api.useCase.GetUserInfoUseCase
import com.example.epic.authRepository.api.useCase.LogoutUseCase
import com.example.epic.common.DEFAULT_BOOLEAN
import com.example.epic.common.DEFAULT_INT
import com.example.epic.coroutine.SingleFlowEvent
import com.example.epic.coroutine.subscribe
import com.example.epic.sharedPrefs.mainFeaturesPrefs.MainFeaturesPreferences
import com.example.epic.ui.base.viewmodel.BaseViewModel
import com.example.epic.ui.base.viewmodel.UiState

class SettingsViewModel(
    private val logoutUseCase: LogoutUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val mainFeaturesPreferences: MainFeaturesPreferences
) : BaseViewModel() {

    private val _logout = SingleFlowEvent<Boolean>()
    val logout = _logout.asFlow()

    fun onLogout() {
        updateState(UiState.LOADING)
        logoutUseCase.execute().subscribe(
            scope = viewModelScope,
            success = {
                updateState(UiState.READY)
                mainFeaturesPreferences.isVPNRunning = DEFAULT_BOOLEAN
                _logout.tryEmitScope(viewModelScope, it)
            },
            error = {
                it.localizedMessage?.let { message ->
                    updateState(UiState.ERROR(message, DEFAULT_INT))
                }
            }
        )
    }

    fun getUserInfo() {
        updateState(UiState.LOADING)
        getUserInfoUseCase.execute().subscribe(
            scope = viewModelScope,
            success = {
                updateState(UiState.READY)
            },
            error = {
                it.localizedMessage?.let { message ->
                    updateState(UiState.ERROR(message, DEFAULT_INT))
                }
            })
    }
}