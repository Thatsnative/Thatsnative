package com.example.epic.di

import com.example.epic.network.interseptors.TokenAuthenticator
import com.example.epic.network.interseptors.TokenInterceptor
import com.example.epic.network.okhttp.createOkHttpClient
import okhttp3.OkHttpClient
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    singleOf(::TokenInterceptor) { bind<TokenInterceptor>() }
    singleOf(::TokenAuthenticator) { bind<TokenAuthenticator>() }

    single<OkHttpClient> { createOkHttpClient() }
}