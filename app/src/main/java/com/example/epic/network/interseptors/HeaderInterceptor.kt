package com.example.epic.network.interseptors

import okhttp3.Interceptor
import okhttp3.Response

private const val CONTENT_TYPE_HEADER = "Content-Type"
private const val CONTENT_TYPE_VALUE = "application/json"

class HeaderInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        return chain.proceed(
            originalRequest.newBuilder()
            .addHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE).build()
        )
    }
}