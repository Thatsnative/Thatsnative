package com.example.epic.authRepository.impl.useCase


import com.example.epic.authRepository.api.model.response.CheckCodeResponse
import com.example.epic.authRepository.api.repository.AuthRepository
import com.example.epic.authRepository.api.useCase.CheckCodeUseCase
import kotlinx.coroutines.flow.Flow

class CheckCodeUseCaseImpl(
    private val authRepository: AuthRepository
) : CheckCodeUseCase {
    override fun invoke(code: String): Flow<CheckCodeResponse> {
        return authRepository.checkCode(code)
    }
}
