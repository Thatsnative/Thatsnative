package com.example.epic.ui.onboarding.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.epic.databinding.OnboardingItemLayoutBinding
import com.example.epic.ui.base.adapter.BaseRecyclerViewAdapter

class OnboardingAdapter() :
    BaseRecyclerViewAdapter<OnboardingItemLayoutBinding, OnboardingItemModel, OnboardingViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OnboardingViewHolder(
        OnboardingItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun getItemCount() = itemList.size
}