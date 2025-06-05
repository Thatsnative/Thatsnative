package com.example.epic.ui.onboarding.adapter

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.epic.ui.base.adapter.DiffUtilModel

data class OnboardingItemModel(
    override val id: Int,
    @StringRes val title: Int,
    @StringRes val descriptor: Int,
    @StringRes val description: Int,
    @DrawableRes val image: Int,
) : DiffUtilModel<Int>()
