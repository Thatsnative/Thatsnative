package com.example.epic.ui.homeFragment.adapter

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.epic.ui.base.adapter.DiffUtilModel

data class HomeFeatureItemModel(
    override val id: Int,
    @DrawableRes val icon: Int,
    @StringRes val title: Int,
    @StringRes val description: Int,
    val isChecked: Boolean
): DiffUtilModel<Int>()
