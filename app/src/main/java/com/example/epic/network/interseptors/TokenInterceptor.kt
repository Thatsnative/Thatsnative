package com.example.epic.network.interseptors

import com.example.epic.sharedPrefs.authPrefs.AuthPreferences
import okhttp3.Interceptor
import okhttp3.Response

private const val AUTHORIZATION_HEADER = "Authorization"
private const val AUTHORIZATION_BEARER = "Bearer"
private val SKIP_LIST = listOf("/api/token/", "/api/register/", "/api/password_reset/")

class TokenInterceptor(
    private val prefs: AuthPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = prefs.getToken()
        val originalRequest = chain.request()
        return if (token.isNotEmpty() && !SKIP_LIST.any { originalRequest.url.toString().contains(it) }) {
            chain.proceed(
                originalRequest.newBuilder()
                    .header(AUTHORIZATION_HEADER, "$AUTHORIZATION_BEARER $token")
                    .build()
            )
        } else {
            chain.proceed(originalRequest)
        }
    }
}