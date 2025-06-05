package com.example.epic.repositories.vpnRepository.impl.useCase

import androidx.paging.PagingData
import com.example.epic.db.entity.HostListItem
import com.example.epic.repositories.vpnRepository.api.repository.VPNRepository
import com.example.epic.repositories.vpnRepository.api.useCase.GetPagedHostListUseCase
import kotlinx.coroutines.flow.Flow

class GetPagedHostListUseCaseImpl(
    private val repository: VPNRepository
) : GetPagedHostListUseCase {

    override fun invoke(): Flow<PagingData<HostListItem>> {
        return repository.getPagedHosts()
    }
}
