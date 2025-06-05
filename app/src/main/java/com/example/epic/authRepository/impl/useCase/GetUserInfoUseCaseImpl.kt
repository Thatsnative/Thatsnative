package com.example.epic.authRepository.impl.useCase

import UserRepository
import com.example.epic.authRepository.api.model.response.UserInfo
import com.example.epic.authRepository.api.useCase.GetUserInfoUseCase
import kotlinx.coroutines.flow.Flow

class GetUserInfoUseCaseImpl(
    private val userRepository: UserRepository
) : GetUserInfoUseCase {

    override fun execute(): Flow<UserInfo> {
        return userRepository.getUserInfo()
    }
}
