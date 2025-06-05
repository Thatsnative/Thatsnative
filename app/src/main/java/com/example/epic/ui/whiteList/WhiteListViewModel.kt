package com.example.epic.ui.whiteList

import androidx.lifecycle.viewModelScope
import com.example.epic.common.DEFAULT_BOOLEAN
import com.example.epic.common.DEFAULT_INT
import com.example.epic.coroutine.SingleFlowEvent
import com.example.epic.coroutine.subscribe
import com.example.epic.repositories.vpnRepository.api.useCase.AllowHostUseCase
import com.example.epic.repositories.vpnRepository.api.useCase.BlockHostUseCase
import com.example.epic.repositories.vpnRepository.api.useCase.GetAllowedHostsUseCase
import com.example.epic.ui.base.viewmodel.BaseViewModel
import com.example.epic.ui.base.viewmodel.UiState
import com.example.epic.ui.whiteList.adapter.WhiteListState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch

class WhiteListViewModel(
    private val allowHostUseCase: AllowHostUseCase,
    private val blockHostUseCase: BlockHostUseCase,
    private val getAllowedHostsUseCase: GetAllowedHostsUseCase
) : BaseViewModel() {
    private val _whiteList = SingleFlowEvent<List<String>>()
    val whiteList = _whiteList.asFlow()

    private val _whiteListState = SingleFlowEvent<WhiteListState>()
    val whiteListState = _whiteListState.asFlow()

    init {
        getAllowedHosts()
    }

    fun getAllowedHosts(filter: String? = null) {
        updateState(UiState.LOADING)
        getAllowedHostsUseCase.execute().subscribe(
            scope = viewModelScope,
            success = { whiteList ->

                val filteredList = if (filter != null) {
                    whiteList.filter { it.contains(filter) }.apply {
                        updateWhiteListState(WhiteListState.CAN_ADD)
                    }
                } else {
                    whiteList.apply {
                        updateWhiteListState(
                            if (isEmpty()) {
                                WhiteListState.EMPTY
                            } else {
                                WhiteListState.READY
                            }
                        )
                    }
                }

                _whiteList.tryEmitScope(viewModelScope, filteredList)
                updateState(UiState.READY)
            },
            error = {
                _whiteList.tryEmitScope(viewModelScope, emptyList())
                updateState(UiState.ERROR(it.localizedMessage, DEFAULT_INT))
            }
        )
    }

    fun onAllowHost(host: String) {
        updateState(UiState.LOADING)
        allowHostUseCase.execute(host).subscribe(
            scope = viewModelScope,
            success = {

            },
            error = {

            }
        )
    }

    fun onBlockHost(host: String) {
        updateState(UiState.LOADING)
        blockHostUseCase.execute(host).subscribe(
            scope = viewModelScope,
            success = {

            },
            error = {

            }
        )
    }

    fun syncWhiteList() {
        // sync db
    }

    fun updateWhiteListState(state: WhiteListState, isEditDoneClick: Boolean = DEFAULT_BOOLEAN) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isEditDoneClick) _whiteListState.tryEmitScope(viewModelScope, WhiteListState.READY)

            if (whiteListState.lastOrNull() == WhiteListState.EDIT && !isEditDoneClick) return@launch

            _whiteListState.tryEmitScope(viewModelScope, state)
        }
    }
}