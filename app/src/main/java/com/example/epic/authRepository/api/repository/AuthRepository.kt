package com.example.epic.authRepository.api.repository

import com.example.epic.authRepository.api.model.response.CheckCodeResponse
import com.example.epic.authRepository.api.model.response.SignInResponse
import com.example.epic.authRepository.api.model.response.Token
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun signIn(email: String): Flow<SignInResponse>
    fun refreshToken(): Flow<Token>
    fun checkCode(code: String): Flow<CheckCodeResponse>
    fun logout(): Flow<Boolean>

}