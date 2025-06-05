package com.example.epic.authRepository.api.useCase

import com.example.epic.authRepository.api.model.response.UserInfo
import kotlinx.coroutines.flow.Flow

interface GetUserInfoUseCase {
    fun execute(): Flow<UserInfo>
}