package com.example.epic.ui.homeFragment

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.view.MotionEvent
import android.view.View.OVER_SCROLL_NEVER
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.epic.R
import com.example.epic.analityc.AnalyticLogger
import com.example.epic.arguments.ArgumentKey.IS_RUNNING_KEY
import com.example.epic.arguments.BundleKey.TOGGLE_BUTTON_KEY
import com.example.epic.common.DEFAULT_BOOLEAN
import com.example.epic.common.PermissionUtils
import com.example.epic.common.setVisibilityTransitionAnimation
import com.example.epic.coroutine.subscribe
import com.example.epic.databinding.HomeFragmentLayoutBinding
import com.example.epic.ui.base.fragment.BaseFragmentNC
import com.example.epic.ui.base.viewmodel.UiState
import com.example.epic.ui.homeFragment.adapter.HomeFeatureAdapter
import com.example.epic.ui.homeFragment.adapter.HomeFeatureItemModel

class HomeFragment :
    BaseFragmentNC<HomeFragmentViewModel, HomeFragmentLayoutBinding>(R.layout.home_fragment_layout) {
    override val viewModel: HomeFragmentViewModel by viewModel()
    override val binding: HomeFragmentLayoutBinding by viewBinding()

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            viewModel.updateProtectionsState()
            if (viewModel.isRunning.value) {
                showHideHelpButton(!viewModel.isAllProtectionsEnabled())
                updateVpnRunningButtonState(true)
            }
        }
    }

    private val presetsAdapter by lazy {
        HomeFeatureAdapter {
            viewModel.onEnhanceYourProtectionSwitchClick(it)
            if (viewModel.isRunning.value) {
                showHideHelpButton(!viewModel.isAllProtectionsEnabled())
                updateVpnRunningButtonState(true)
            }
        }
    }
    private val annoyancesAdapter by lazy {
        HomeFeatureAdapter {
            viewModel.onExploreFeaturesSwitchClick(it)
            if (viewModel.isRunning.value) {
                showHideHelpButton(!viewModel.isAllProtectionsEnabled())
                updateVpnRunningButtonState(true)
            }
        }
    }

    override fun initView() {
        with(binding) {
            annoyances.title.setText(R.string.annoyances)
            presets.title.setText(R.string.presets)
            val presetsLayoutManager = object : LinearLayoutManager(context) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
            val annoyancesLayoutManager = object : LinearLayoutManager(context) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
            presets.verticalRecyclerView.layoutManager = presetsLayoutManager
            presets.verticalRecyclerView.overScrollMode = OVER_SCROLL_NEVER
            annoyances.verticalRecyclerView.layoutManager = annoyancesLayoutManager
            annoyances.verticalRecyclerView.overScrollMode = OVER_SCROLL_NEVER
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initListeners() {
        with(binding) {
            container.setOnTouchListener { _, event ->
                val isClicked = viewModel.isRunning.value &&
                        binding.allProtectionDialog.root.isVisible && event.action == MotionEvent.ACTION_UP
                if (isClicked) {
                    showHideTurnOnAllProtectionsOnDialog()
                }
                isClicked
            }
            turnButton.setOnClickListener {
                if (isAllPermissionsGranted()) {
                    viewModel.onToggleButtonClick()
                } else {
                    navigateFragment(R.id.action_tabFragment_to_helpFragment)
                }
            }
            allProtectionDialog.turnButton.setOnClickListener {
                showHideTurnOnAllProtectionsOnDialog()
                showHideHelpButton()
                viewModel.updateAllProtections()
                updateVpnRunningButtonState(true)
            }
            helpButton.setOnClickListener {
                showHideTurnOnAllProtectionsOnDialog(true)
            }
            needHelp.root.setOnClickListener {
                navigateFragment(R.id.action_tabFragment_to_helpFragment)
            }
        }
    }

    override fun collectFlows() {
        with(viewModel) {
            enhanceYourProtectionList.subscribe(viewLifecycleOwner, ::initPresets)
            exploreFeaturesList.subscribe(viewLifecycleOwner, ::initAnnoyances)
            toggleVpnButton.subscribe(viewLifecycleOwner) {
                requireActivity().supportFragmentManager.setFragmentResult(
                    TOGGLE_BUTTON_KEY,
                    bundleOf(IS_RUNNING_KEY to isRunning.value)
                )
            }
            isRunning.subscribe(viewLifecycleOwner) {
                updateVpnRunningButtonState(it)
                if(it){
                    updateAllProtections(it)
                }
            }
            adBlockCount.subscribe(viewLifecycleOwner) {
                getString(R.string.blocked_ads, it).run {
                    binding.adBlockCount.text = this@run
                    AnalyticLogger.info(this@run)
                }
            }
            isNeedHelpVisible.subscribe(viewLifecycleOwner) {
                binding.needHelp.root.isVisible = it
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateProtectionsState()
        viewModel.updateNeedHelpState(isAllPermissionsGranted())
    }

    override fun onSuccess() {}

    override fun onLoading() {}

    override fun onError(message: String, code: Int) {}

    private fun initPresets(
        enhanceYourProtectionItems: List<HomeFeatureItemModel>?
    ) {
        with(binding.presets.verticalRecyclerView) {
            adapter = presetsAdapter
            enhanceYourProtectionItems?.let(presetsAdapter::submitList)
        }
    }

    private fun initAnnoyances(exploreFeaturesItems: List<HomeFeatureItemModel>?) {
        with(binding.annoyances.verticalRecyclerView) {
            adapter = annoyancesAdapter
            exploreFeaturesItems?.let(annoyancesAdapter::submitList)
            viewModel.updateState(UiState.READY)
        }
    }


    private fun showHideTurnOnAllProtectionsOnDialog(isVisible: Boolean = DEFAULT_BOOLEAN) {
        binding.allProtectionDialog.root.setVisibilityTransitionAnimation(isVisible)
        binding.container.setVisibilityTransitionAnimation(isVisible)
    }

    private fun showHideHelpButton(isVisible: Boolean = DEFAULT_BOOLEAN) {
        binding.helpButton.setVisibilityTransitionAnimation(isVisible)
    }

    private fun updateVpnRunningButtonState(isRunning: Boolean) {
        val (buttonAnimation, protectionStateText) = when {
            isRunning && !viewModel.isAllProtectionsEnabled() -> {
                showHideHelpButton(true)
                R.raw.vpn_on_warn to R.string.protection_is_active
            }

            isRunning -> R.raw.vpn_on to R.string.protection_is_active
            else -> {
                showHideHelpButton()
                R.raw.vpn_off to R.string.protection_is_disabled
            }
        }
        binding.turnButton.setAnimation(buttonAnimation)
        binding.protectionState.setText(protectionStateText)
    }

    private fun isAllPermissionsGranted() =
        PermissionUtils.getVpnPermission(requireActivity()) == null &&
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    !PermissionUtils.checkPermission(
                        requireActivity(),
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                } else {
                    true
                }

    companion object {
        val TAG = HomeFragment::class.java.simpleName
        fun newInstance() = HomeFragment()
    }
}