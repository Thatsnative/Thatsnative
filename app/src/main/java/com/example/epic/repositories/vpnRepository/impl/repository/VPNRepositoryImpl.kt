package com.example.epic.repositories.vpnRepository.impl.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.epic.coroutine.emitFlow
import com.example.epic.db.dao.HostEntryDao
import com.example.epic.db.dao.HostListItemDao
import com.example.epic.db.entity.HostListItem
import com.example.epic.db.entity.ListType
import com.example.epic.repositories.vpnRepository.api.repository.VPNRepository
import kotlinx.coroutines.flow.Flow

class VPNRepositoryImpl(
    private val hostListItemDao: HostListItemDao,
    private val hostEntryDao: HostEntryDao
    ) : VPNRepository {
    override fun getPagedHosts(): Flow<PagingData<HostListItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { hostListItemDao.getAllPaged() }
        ).flow
    }

    override fun getAllowedHosts() = hostEntryDao.getEnabledAllowedHostsFlow()

    override fun allowHost(host: String) = emitFlow {
        HostListItem().run {
            this.host = host
            type = ListType.ALLOWED
            isEnabled = true

            val id = hostListItemDao.getHostId(host)
            if (id.isPresent) {
                this.id = id.get()
                hostListItemDao.update(this)
            } else {
                hostListItemDao.insert(this)
            }
        }
    }

    override fun blockHost(host: String) = emitFlow {
        HostListItem().run {
            this.host = host
            type = ListType.BLOCKED
            isEnabled = true

            val id = hostListItemDao.getHostId(host)
            if (id.isPresent) {
                this.id = id.get()
                hostListItemDao.update(this)
            } else {
                hostListItemDao.insert(this)
            }
        }
    }
}
