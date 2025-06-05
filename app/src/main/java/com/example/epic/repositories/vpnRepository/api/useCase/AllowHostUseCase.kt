package com.example.epic.repositories.vpnRepository.api.useCase

import kotlinx.coroutines.flow.Flow

interface AllowHostUseCase {
    fun execute(host: String): Flow<Unit>
}