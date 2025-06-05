package com.example.epic.network.interseptors

import com.example.epic.authRepository.api.useCase.RefreshTokenUseCase
import com.example.epic.common.EMPTY_STRING
import com.example.epic.common.inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.net.HttpURLConnection

private const val AUTHORIZATION_HEADER = "Authorization"

class TokenAuthenticator : Authenticator {
    private val refreshTokenUseCase by inject<RefreshTokenUseCase>()

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
            var token: String
            runBlocking {
                token = try {
                    refreshTokenUseCase.execute().first().accessToken
                } catch (exception: Exception) {
                    EMPTY_STRING
                }
            }

            return if (token.isNotEmpty()) {
                response.request.newBuilder()
                    .header(AUTHORIZATION_HEADER, token)
                    .build()
            } else {
                null
            }
        } else {
            return response.request.newBuilder().build()
        }
    }
}