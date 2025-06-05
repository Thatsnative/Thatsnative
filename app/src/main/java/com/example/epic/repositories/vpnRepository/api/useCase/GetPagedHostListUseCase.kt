package com.example.epic.repositories.vpnRepository.api.useCase

import androidx.paging.PagingData
import com.example.epic.db.entity.HostListItem
import kotlinx.coroutines.flow.Flow

interface GetPagedHostListUseCase {
    operator fun invoke(): Flow<PagingData<HostListItem>>
}
