package com.example.epic.ui.authorization

import androidx.lifecycle.viewModelScope
import com.example.epic.authRepository.api.useCase.SignInUseCase
import com.example.epic.common.DEFAULT_BOOLEAN
import com.example.epic.coroutine.SingleFlowEvent
import com.example.epic.coroutine.subscribe
import com.example.epic.sharedPrefs.authPrefs.AuthPreferences
import com.example.epic.ui.base.viewmodel.BaseViewModel
import com.example.epic.ui.base.viewmodel.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

const val LOGIN_ERROR_CODE = 403
const val EMAIL_ERROR_CODE = 1

class AuthViewModel(
    private val signInUseCase: SignInUseCase,
    private val authPreferences: AuthPreferences
) : BaseViewModel() {
    private val _isEmailCleanupVisible = MutableStateFlow(DEFAULT_BOOLEAN)
    val isEmailCleanupVisible: StateFlow<Boolean> = _isEmailCleanupVisible

    private val _isLoginSuccess = SingleFlowEvent<String>()
    val isLoginSuccess = _isLoginSuccess.asFlow()


    fun login(email: String) {
        updateState(UiState.LOADING)
        signInUseCase(email).subscribe(
            scope = viewModelScope,
            success = { response->
                if (response.success) {
                    _isLoginSuccess.tryEmitScope(viewModelScope, email)
                    authPreferences.saveEmail(email)
                    updateState(UiState.READY)
                } else {
                    authPreferences.clearEmail()
                    updateState(
                        UiState.ERROR(
                            response.error ?: "Unexpected error",
                            EMAIL_ERROR_CODE
                        ))
                }
            },
            error = { error ->
                error.message?.let { updateState(UiState.ERROR(it, LOGIN_ERROR_CODE)) }
                authPreferences.clearEmail()
            }
        )
    }

    fun updateCleanupEmailVisibility(isVisible: Boolean) {
        _isEmailCleanupVisible.value = isVisible
    }
}