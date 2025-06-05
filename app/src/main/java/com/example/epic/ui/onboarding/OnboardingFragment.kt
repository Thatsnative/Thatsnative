package com.example.epic.ui.onboarding

import org.koin.androidx.viewmodel.ext.android.viewModel
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.epic.R
import com.example.epic.databinding.OnboardingFragmentLayoutBinding
import com.example.epic.ui.base.fragment.BaseFragmentNC
import com.example.epic.ui.onboarding.adapter.OnboardingAdapter
import com.example.epic.ui.onboarding.adapter.onBoardingItems

class OnboardingFragment :
    BaseFragmentNC<OnboardingViewModel, OnboardingFragmentLayoutBinding>(R.layout.onboarding_fragment_layout) {
    override val viewModel: OnboardingViewModel by viewModel()
    override val binding: OnboardingFragmentLayoutBinding by viewBinding()
    private val onboardingAdapter = OnboardingAdapter()

    override fun initView() {
        initViewPager()
    }

    override fun initListeners() {
        with(binding) {
            skipButton.setOnClickListener {
                popUpToFragment(
                    R.id.action_onboardingFragment_to_authFragment,
                    null,
                    R.id.onboardingFragment
                )
            }
        }
    }

    override fun collectFlows() {

    }

    override fun onSuccess() {

    }

    override fun onLoading() {

    }

    override fun onError(message: String, code: Int) {

    }

    private fun initViewPager() {
        with(binding.onboardingPager) {
            adapter = onboardingAdapter
            onboardingAdapter.submitList(onBoardingItems)
            binding.indicator.initWithViewPager(this)
        }
    }
}