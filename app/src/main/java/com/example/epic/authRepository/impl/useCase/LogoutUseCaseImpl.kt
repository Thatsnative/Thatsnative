package com.example.epic.authRepository.impl.useCase

import com.example.epic.authRepository.api.repository.AuthRepository
import com.example.epic.authRepository.api.useCase.LogoutUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class LogoutUseCaseImpl(private val authRepository: AuthRepository) : LogoutUseCase {
    override fun execute() = authRepository.logout().flowOn(Dispatchers.IO)
}