package com.example.epic.ui.authorization

import androidx.lifecycle.viewModelScope
import com.example.epic.authRepository.api.useCase.CheckCodeUseCase
import com.example.epic.authRepository.api.useCase.SignInUseCase
import com.example.epic.common.DEFAULT_BOOLEAN
import com.example.epic.coroutine.SingleFlowEvent
import com.example.epic.coroutine.subscribe
import com.example.epic.sharedPrefs.authPrefs.AuthPreferences
import com.example.epic.ui.base.viewmodel.BaseViewModel
import com.example.epic.ui.base.viewmodel.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject
import retrofit2.HttpException

class CheckCodeViewModel(
    private val checkCodeUseCase: CheckCodeUseCase,
    private val signInUseCase: SignInUseCase,
    private val authPreferences: AuthPreferences
) : BaseViewModel() {
    private val _codeCleanupVisible = MutableStateFlow(DEFAULT_BOOLEAN)
    val codeCleanupVisible: StateFlow<Boolean> = _codeCleanupVisible

    private val _isLoginSuccess = SingleFlowEvent<Boolean>()
    val isLoginSuccess = _isLoginSuccess.asFlow()

    private val _isCodeSend = SingleFlowEvent<Boolean>()
    val isCodeSend = _isCodeSend.asFlow()


    fun checkCode(code: String) {
        updateState(UiState.LOADING)
        checkCodeUseCase(code).subscribe(
            scope = viewModelScope,
            success = { response ->
                if (response.error.isNullOrEmpty() && response.accessToken != null && response.refreshToken != null) {
                    authPreferences.saveToken(
                        response.accessToken,
                        response.refreshToken
                    )
                    _isLoginSuccess.tryEmitScope(viewModelScope, true)
                    updateState(UiState.READY)
                } else {
                    updateState(
                        UiState.ERROR(
                            response.error ?: "Unexpected error",
                            EMAIL_ERROR_CODE
                        )
                    )
                }
            },
            error = { error ->
                if (error is HttpException) {
                    val errorMessage = try {
                        val errorJson = error.response()?.errorBody()?.string()
                        val parsedMessage =
                            JSONObject(errorJson ?: "{}").optString("error", "Unknown error")
                        parsedMessage
                    } catch (e: Exception) {
                        "Unexpected error occurred"
                    }

                    updateState(UiState.ERROR(errorMessage, EMAIL_ERROR_CODE))
                } else {
                    updateState(
                        UiState.ERROR(
                            error.message ?: "Unexpected error",
                            EMAIL_ERROR_CODE
                        )
                    )
                }
            }
        )
    }

    fun sendCode() {
        updateState(UiState.LOADING)
        signInUseCase(authPreferences.getEmail()).subscribe(
            scope = viewModelScope,
            success = { response ->
                if (response.success) {
                    _isCodeSend.tryEmitScope(viewModelScope, true)
                    updateState(UiState.READY)
                } else {
                    updateState(
                        UiState.ERROR(
                            response.error ?: "Error resend code",
                            LOGIN_ERROR_CODE
                        )
                    )
                }
            },
            error = { error ->
                error.message?.let { updateState(UiState.ERROR(it, LOGIN_ERROR_CODE)) }
            }
        )
    }

    fun updateCleanupCodeVisibility(isVisible: Boolean) {
        _codeCleanupVisible.value = isVisible
    }
}