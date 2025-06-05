package com.example.epic.repositories.vpnRepository.impl.useCase

import com.example.epic.repositories.vpnRepository.api.repository.VPNRepository
import com.example.epic.repositories.vpnRepository.api.useCase.BlockHostUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class BlockHostUseCaseImpl(private val vpnRepository: VPNRepository) : BlockHostUseCase {
    override fun execute(host: String) = vpnRepository.blockHost(host).flowOn(Dispatchers.IO)
}