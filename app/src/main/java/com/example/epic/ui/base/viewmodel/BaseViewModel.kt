package com.example.epic.ui.base.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow

abstract class BaseViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.LOADING)
    val uiState: StateFlow<UiState> = _uiState

    fun updateState(newState: UiState) {
        _uiState.value = newState
    }

    open fun onDetach() {

    }
}