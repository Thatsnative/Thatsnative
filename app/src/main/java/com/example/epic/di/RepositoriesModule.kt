package com.example.epic.di

import UserRepository
import com.example.epic.authRepository.api.repository.AuthRepository
import com.example.epic.authRepository.impl.repository.AuthRepositoryImpl
import com.example.epic.authRepository.impl.repository.UserRepositoryImpl
import com.example.epic.repositories.vpnRepository.api.repository.VPNRepository
import com.example.epic.repositories.vpnRepository.impl.repository.VPNRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.bind
import org.koin.dsl.module

val repositoriesModule = module {
    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>() }
    singleOf(::VPNRepositoryImpl) { bind<VPNRepository>() }
    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
}