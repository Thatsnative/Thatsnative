package com.example.epic.authRepository.api.useCase

import kotlinx.coroutines.flow.Flow

interface LogoutUseCase {
    fun execute(): Flow<Boolean>
}