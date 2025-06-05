package com.example.epic.authRepository.impl.repository

import com.example.epic.authRepository.api.model.requests.CheckCodeRequest
import com.example.epic.authRepository.api.model.requests.LogoutRequest
import com.example.epic.authRepository.api.model.requests.RefreshTokenRequest
import com.example.epic.authRepository.api.model.requests.SignInRequest
import com.example.epic.authRepository.api.model.response.CheckCodeResponse
import com.example.epic.authRepository.api.model.response.SignInResponse
import com.example.epic.authRepository.api.repository.AuthRepository
import com.example.epic.authRepository.impl.services.AuthService
import com.example.epic.coroutine.emitFlow
import com.example.epic.sharedPrefs.authPrefs.AuthPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AuthRepositoryImpl(
    private val authService: AuthService,
    private val authPreferences: AuthPreferences
) : AuthRepository {
    override fun signIn(email: String): Flow<SignInResponse> = flow {
        val request = SignInRequest(email)
        val response = authService.signIn(request)
        emit(response)
    }.flowOn(Dispatchers.IO)

    override fun refreshToken() = emitFlow {
        authService.refreshToken(RefreshTokenRequest(authPreferences.getRefreshToken())).also {
            authPreferences.saveToken(it.accessToken, it.refreshToken.orEmpty())
        }
    }

    override fun logout() = emitFlow {
        try {
            val request = LogoutRequest(token = authPreferences.getRefreshToken())
            authService.logout(request)
            authPreferences.clearProfileId()
            authPreferences.clearToken()
            authPreferences.clearUserId()
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun checkCode(code: String): Flow<CheckCodeResponse> = flow {
        val request = CheckCodeRequest(authPreferences.getEmail(), code)
        val response = authService.checkCode(request)
        emit(response)
    }.flowOn(Dispatchers.IO)
}