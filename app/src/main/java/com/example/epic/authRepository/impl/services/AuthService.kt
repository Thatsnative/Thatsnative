package com.example.epic.authRepository.impl.services

import com.example.epic.authRepository.api.model.requests.CheckCodeRequest
import com.example.epic.authRepository.api.model.requests.LogoutRequest
import com.example.epic.authRepository.api.model.requests.RefreshTokenRequest
import com.example.epic.authRepository.api.model.requests.SignInRequest
import com.example.epic.authRepository.api.model.response.CheckCodeResponse
import com.example.epic.authRepository.api.model.response.SignInResponse
import com.example.epic.authRepository.api.model.response.SuccessResponse
import com.example.epic.authRepository.api.model.response.Token
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthService {
    @POST("api/token/")
    suspend fun signIn(@Body authRequest: SignInRequest): SignInResponse

    @POST("api/token/refresh/")
    suspend fun refreshToken(@Body refreshTokenRequest: RefreshTokenRequest): Token

    @POST("api/user/check_code/")
    suspend fun checkCode(
        @Body body: CheckCodeRequest
    ): CheckCodeResponse

    @Headers("Content-Type: application/json")
    @POST("api/user/logout/")
    suspend fun logout(@Body request: LogoutRequest)
}