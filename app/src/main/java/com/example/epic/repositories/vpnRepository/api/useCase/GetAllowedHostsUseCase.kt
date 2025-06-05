package com.example.epic.repositories.vpnRepository.api.useCase

import kotlinx.coroutines.flow.Flow

interface GetAllowedHostsUseCase {
    fun execute(): Flow<List<String>>
}