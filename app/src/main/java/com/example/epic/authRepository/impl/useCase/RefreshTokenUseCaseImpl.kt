package com.example.epic.authRepository.impl.useCase

import com.example.epic.authRepository.api.repository.AuthRepository
import com.example.epic.authRepository.api.useCase.RefreshTokenUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class RefreshTokenUseCaseImpl(private val authRepository: AuthRepository) :
    RefreshTokenUseCase {
    override fun execute() = authRepository.refreshToken().flowOn(Dispatchers.IO)
}