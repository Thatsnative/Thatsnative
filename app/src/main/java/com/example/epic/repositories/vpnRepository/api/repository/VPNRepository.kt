package com.example.epic.repositories.vpnRepository.api.repository

import androidx.paging.PagingData
import com.example.epic.db.entity.HostListItem
import kotlinx.coroutines.flow.Flow

interface VPNRepository {
    fun getPagedHosts(): Flow<PagingData<HostListItem>>
    fun getAllowedHosts(): Flow<List<String>>
    fun allowHost(host: String): Flow<Unit>
    fun blockHost(host: String): Flow<Unit>
}