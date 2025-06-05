package com.example.epic.repositories.vpnRepository.impl.useCase

import com.example.epic.repositories.vpnRepository.api.repository.VPNRepository
import com.example.epic.repositories.vpnRepository.api.useCase.GetAllowedHostsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class GetAllowedHostsUseCaseImpl(private val vpnRepository: VPNRepository) :
    GetAllowedHostsUseCase {
    override fun execute() = vpnRepository.getAllowedHosts().flowOn(Dispatchers.IO)
}