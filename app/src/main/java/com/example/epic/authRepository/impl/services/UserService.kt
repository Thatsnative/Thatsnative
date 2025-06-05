package com.example.epic.authRepository.impl.services

import com.example.epic.authRepository.api.model.requests.RefreshTokenRequest
import com.example.epic.authRepository.api.model.requests.UpdateUserRequest
import com.example.epic.authRepository.api.model.response.UpdateUserResponse
import com.example.epic.authRepository.api.model.response.UserInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface UserService {

    @DELETE("api/user/delete/")
    suspend fun deleteUser(
        @Header("Authorization") token: String,
        @Body body: RefreshTokenRequest
    ): Unit

    @POST("api/user/update/")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Body body: UpdateUserRequest
    ): UpdateUserResponse


    @GET("api/user/info/")
    suspend fun getUserInfo(
        @Header("Authorization") token: String
    ): UserInfo

}