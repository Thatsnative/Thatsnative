package com.example.epic.ui.blockListFragment

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.epic.db.entity.HostListItem
import com.example.epic.repositories.vpnRepository.api.useCase.GetPagedHostListUseCase
import com.example.epic.ui.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.Flow

class BlockListViewModel(
    private val getPagedHostListUseCase: GetPagedHostListUseCase
): BaseViewModel()  {
        val pagedHosts: Flow<PagingData<HostListItem>> =
            getPagedHostListUseCase()
                .cachedIn(viewModelScope)
}