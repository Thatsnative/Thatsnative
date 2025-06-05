package com.example.epic.authRepository.impl.useCase

import com.example.epic.authRepository.api.model.response.SignInResponse
import com.example.epic.authRepository.api.repository.AuthRepository
import com.example.epic.authRepository.api.useCase.SignInUseCase
import kotlinx.coroutines.flow.Flow

class SignInUseCaseImpl(
    private val authRepository: AuthRepository
) : SignInUseCase {
    override fun invoke(email: String): Flow<SignInResponse> {
        return authRepository.signIn(email)
    }
}