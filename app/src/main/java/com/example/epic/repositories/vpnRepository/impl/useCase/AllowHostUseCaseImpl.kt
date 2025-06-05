package com.example.epic.repositories.vpnRepository.impl.useCase

import com.example.epic.repositories.vpnRepository.api.repository.VPNRepository
import com.example.epic.repositories.vpnRepository.api.useCase.AllowHostUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class AllowHostUseCaseImpl(private val vpnRepository: VPNRepository) : AllowHostUseCase {
    override fun execute(host: String) = vpnRepository.allowHost(host).flowOn(Dispatchers.IO)
}