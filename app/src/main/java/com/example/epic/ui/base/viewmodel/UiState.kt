package com.example.epic.ui.base.viewmodel

sealed class UiState {
    data object READY : UiState()
    data object LOADING : UiState()
    data class ERROR(val message: String, val code: Int) : UiState()
}

object ErrorCode {

}