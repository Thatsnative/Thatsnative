package com.example.epic.ui.settingsfragment

import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.epic.BuildConfig
import com.example.epic.R
import com.example.epic.common.PRIVACY_POLICY
import com.example.epic.common.SUPPORT
import com.example.epic.common.TERMS
import com.example.epic.common.openExternalUrl
import com.example.epic.common.orDefault
import com.example.epic.coroutine.subscribe
import com.example.epic.databinding.SettingsFragmentLayoutBinding
import com.example.epic.ui.base.fragment.BaseFragmentNC
import com.example.epic.ui.base.viewmodel.UiState
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment :
    BaseFragmentNC<SettingsViewModel, SettingsFragmentLayoutBinding>(R.layout.settings_fragment_layout) {
    override val viewModel: SettingsViewModel by viewModel()
    override val binding: SettingsFragmentLayoutBinding by viewBinding()

    override fun initView() {
        initTitles()
        viewModel.updateState(UiState.READY)
    }

    override fun initListeners() {
        with(binding) {
            whiteListSettings.root.setOnClickListener {
//                navigateFragment(R.id.action_tabFragment_to_whiteListFragment)
            }
            adNotWorking.root.setOnClickListener { requireContext().openExternalUrl(SUPPORT) }
            eula.root.setOnClickListener { requireContext().openExternalUrl(TERMS) }
            privacyPolicy.root.setOnClickListener { requireContext().openExternalUrl(PRIVACY_POLICY) }
            appVersion.root.setOnClickListener { }
            logOutButton.setOnClickListener { viewModel.onLogout() }
        }
    }

    override fun collectFlows() {
        with(viewModel) {
            logout.subscribe(lifecycleScope) {
                if (it.orDefault()) {
                    popUpToFragment(R.id.action_tabFragment_to_authFragment, null, R.id.tabFragment)
                }
            }
        }
    }

    override fun onSuccess() {

    }

    override fun onLoading() {

    }

    override fun onError(message: String, code: Int) {

    }

    private fun initTitles() {
        with(binding) {
            adNotWorking.title.setText(R.string.ad_blocking_not_working)
            eula.title.setText(R.string.eula)
            privacyPolicy.title.setText(R.string.privacy_policy)
            appVersion.version.text = BuildConfig.VERSION_NAME
        }
    }

    companion object {
        val TAG = SettingsFragment::class.java.simpleName
        fun newInstance() = SettingsFragment()
    }
}