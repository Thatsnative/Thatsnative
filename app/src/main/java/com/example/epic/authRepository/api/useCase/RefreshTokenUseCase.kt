package com.example.epic.authRepository.api.useCase

import com.example.epic.authRepository.api.model.response.Token
import kotlinx.coroutines.flow.Flow

interface RefreshTokenUseCase {
    fun execute(): Flow<Token>
}