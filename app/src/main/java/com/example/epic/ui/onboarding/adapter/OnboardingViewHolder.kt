package com.example.epic.ui.onboarding.adapter

import android.content.Context
import com.example.epic.databinding.OnboardingItemLayoutBinding
import com.example.epic.ui.base.adapter.BaseViewHolder

class OnboardingViewHolder(private val binding: OnboardingItemLayoutBinding) :
    BaseViewHolder<OnboardingItemModel, OnboardingItemLayoutBinding>(binding) {
    override fun bind(item: OnboardingItemModel, context: Context) {
        with(binding) {
            onboardingImage.setImageResource(item.image)
            descriptor.setText(item.descriptor)
            title.setText(item.title)
            description.setText(item.description)
        }
    }
}