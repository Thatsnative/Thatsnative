package com.example.epic.repositories.vpnRepository.api.useCase

import kotlinx.coroutines.flow.Flow

interface BlockHostUseCase {
    fun execute(host: String): Flow<Unit>
}