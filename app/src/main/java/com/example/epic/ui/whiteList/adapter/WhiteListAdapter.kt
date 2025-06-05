package com.example.epic.ui.whiteList.adapter

import com.example.epic.common.DEFAULT_BOOLEAN

class WhiteListAdapter(
    private val onBlockHost: (String) -> Unit,
) {
    private var isEditMode = DEFAULT_BOOLEAN


    fun updateEditMode(isEditMode: Boolean) {
        this.isEditMode = isEditMode
        // notify adapter that data set has changed
    }
}