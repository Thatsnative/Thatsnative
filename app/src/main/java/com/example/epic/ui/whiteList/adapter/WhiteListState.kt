package com.example.epic.ui.whiteList.adapter

sealed class WhiteListState {
    data object READY: WhiteListState()
    data object EMPTY: WhiteListState()
    data object CAN_ADD: WhiteListState()
    data object EDIT: WhiteListState()
}