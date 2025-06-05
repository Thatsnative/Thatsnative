package com.example.epic.di

import com.example.epic.BuildConfig
import com.example.epic.network.retrofit.ServiceBuilder
import com.example.epic.authRepository.impl.services.AuthService
import com.example.epic.authRepository.impl.services.UserService
import org.koin.dsl.module

val serviceModule = module {
    single<AuthService> {
        ServiceBuilder.build(BuildConfig.BASE_URL, AuthService::class.java)
    }
    single<UserService> {
        ServiceBuilder.build(BuildConfig.BASE_URL, UserService::class.java)
    }
}