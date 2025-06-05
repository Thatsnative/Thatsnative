package com.example.epic.authRepository.impl.repository

import UserRepository
import com.example.epic.authRepository.api.model.requests.RefreshTokenRequest
import com.example.epic.authRepository.api.model.requests.UpdateUserRequest
import com.example.epic.authRepository.api.model.response.UpdateUserResponse
import com.example.epic.authRepository.api.model.response.UserInfo
import com.example.epic.authRepository.impl.services.UserService
import com.example.epic.sharedPrefs.authPrefs.AuthPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
class UserRepositoryImpl(
    private val userService: UserService,
    private val authPreferences: AuthPreferences
) : UserRepository {

    override fun deleteUser(authToken: String, refreshToken: String): Flow<Unit> = flow {
        val token = "Bearer $authToken"
        userService.deleteUser(token, RefreshTokenRequest(refreshToken))
        emit(Unit)
    }.flowOn(Dispatchers.IO)

    override fun updateUser(
        authToken: String,
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Flow<UpdateUserResponse> = flow {
        val request = UpdateUserRequest(firstName, lastName, email, password)
        val response = userService.updateUser("Bearer $authToken", request)
        emit(response)
    }.flowOn(Dispatchers.IO)

    override fun getUserInfo(): Flow<UserInfo> = flow {
        val token = "Bearer ${authPreferences.getToken()}"
        val userInfo = userService.getUserInfo(token)
        authPreferences.saveUserId(userInfo.id)
        emit(userInfo)
    }.flowOn(Dispatchers.IO)
}
