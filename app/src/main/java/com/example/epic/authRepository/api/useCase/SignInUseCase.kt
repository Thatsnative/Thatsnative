package com.example.epic.authRepository.api.useCase

import com.example.epic.authRepository.api.model.response.SignInResponse
import kotlinx.coroutines.flow.Flow

interface SignInUseCase {
    operator fun invoke(email: String): Flow<SignInResponse>
}