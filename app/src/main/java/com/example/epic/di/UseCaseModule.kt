package com.example.epic.di

import com.example.epic.authRepository.api.useCase.CheckCodeUseCase
import com.example.epic.authRepository.api.useCase.GetUserInfoUseCase
import com.example.epic.authRepository.api.useCase.LogoutUseCase
import com.example.epic.authRepository.api.useCase.RefreshTokenUseCase
import com.example.epic.authRepository.api.useCase.SignInUseCase
import com.example.epic.authRepository.impl.useCase.SignInUseCaseImpl
import com.example.epic.authRepository.impl.useCase.CheckCodeUseCaseImpl
import com.example.epic.authRepository.impl.useCase.RefreshTokenUseCaseImpl
import com.example.epic.authRepository.impl.useCase.LogoutUseCaseImpl
import com.example.epic.authRepository.impl.useCase.GetUserInfoUseCaseImpl
import com.example.epic.repositories.vpnRepository.api.useCase.AllowHostUseCase
import com.example.epic.repositories.vpnRepository.api.useCase.BlockHostUseCase
import com.example.epic.repositories.vpnRepository.api.useCase.GetAllowedHostsUseCase
import com.example.epic.repositories.vpnRepository.api.useCase.GetPagedHostListUseCase
import com.example.epic.repositories.vpnRepository.impl.useCase.GetPagedHostListUseCaseImpl
import com.example.epic.repositories.vpnRepository.impl.useCase.BlockHostUseCaseImpl
import com.example.epic.repositories.vpnRepository.impl.useCase.AllowHostUseCaseImpl
import com.example.epic.repositories.vpnRepository.impl.useCase.GetAllowedHostsUseCaseImpl
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.bind
import org.koin.dsl.module

val useCaseModule = module {
    singleOf(::SignInUseCaseImpl) { bind<SignInUseCase>() }
    singleOf(::CheckCodeUseCaseImpl) { bind<CheckCodeUseCase>() }
    singleOf(::RefreshTokenUseCaseImpl) { bind<RefreshTokenUseCase>() }
    singleOf(::GetPagedHostListUseCaseImpl) { bind<GetPagedHostListUseCase>() }
    singleOf(::LogoutUseCaseImpl) { bind<LogoutUseCase>() }
    singleOf(::GetUserInfoUseCaseImpl) { bind<GetUserInfoUseCase>() }
    singleOf(::AllowHostUseCaseImpl) { bind<AllowHostUseCase>() }
    singleOf(::BlockHostUseCaseImpl) { bind<BlockHostUseCase>() }
    singleOf(::GetAllowedHostsUseCaseImpl) { bind<GetAllowedHostsUseCase>() }
}