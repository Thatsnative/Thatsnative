package com.example.epic.authRepository.api.useCase


import com.example.epic.authRepository.api.model.response.CheckCodeResponse
import kotlinx.coroutines.flow.Flow

interface CheckCodeUseCase {
    operator fun invoke(code: String): Flow<CheckCodeResponse>
}
