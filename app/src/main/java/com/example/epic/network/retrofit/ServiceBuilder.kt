package com.example.epic.network.retrofit

import com.example.epic.common.inject
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {
    fun <T> build(
        baseUrl: String,
        classType: Class<T>
    ): T {
        val okHttpClient by inject<OkHttpClient>()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(classType)
    }
}