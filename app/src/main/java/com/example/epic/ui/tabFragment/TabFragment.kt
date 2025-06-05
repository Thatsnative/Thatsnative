package com.example.epic.ui.tabFragment

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.epic.R
import com.example.epic.arguments.ArgumentKey.SCREEN_BOTTOM_INSET_KEY
import com.example.epic.arguments.BundleKey.BOTTOM_BAR_INSETS_KEY
import com.example.epic.common.DEFAULT_INT
import com.example.epic.coroutine.subscribe
import com.example.epic.databinding.TabFragmentLayoutBinding
import com.example.epic.ui.base.fragment.BaseFragmentNC
import com.example.epic.ui.homeFragment.HomeFragment
import com.example.epic.ui.settingsfragment.SettingsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class TabFragment :
    BaseFragmentNC<TabViewModel, TabFragmentLayoutBinding>(R.layout.tab_fragment_layout) {
    override val viewModel: TabViewModel by viewModel()
    override val binding: TabFragmentLayoutBinding by viewBinding()

    @IdRes
    private var currentTab: Int? = null
    private var currentFragment: Fragment? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!childFragmentManager.hasCurrentFragment(HomeFragment.TAG)) {
            currentFragment = HomeFragment.newInstance()
            childFragmentManager.beginTransaction()
                .add(R.id.tab_container, requireNotNull(currentFragment), HomeFragment.TAG)
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        updateBottomInsets(DEFAULT_INT)
    }

    override fun onPause() {
        super.onPause()
        updateBottomInsets(-1)
    }

    override fun initView() {
        with(binding) {
            bottomNavigation.setOnNavigationItemSelectedListener { item ->
                if (item.itemId != currentTab) {
                    childFragmentManager.beginTransaction().apply {
                        val newFragment = when (item.itemId) {
                            R.id.mainFragment -> {
                                if (!childFragmentManager.hasCurrentFragment(HomeFragment.TAG)) {
                                    HomeFragment.newInstance()
                                        .apply { add(R.id.tab_container, this, HomeFragment.TAG) }
                                } else {
                                    childFragmentManager.findFragmentByTag(HomeFragment.TAG)
                                }
                            }

//                            R.id.blockListFragment -> {
//                                if (!childFragmentManager.hasCurrentFragment(BlockListFragment.TAG)) {
//                                    BlockListFragment.newInstance()
//                                        .apply {
//                                            add(
//                                                R.id.tab_container,
//                                                this,
//                                                BlockListFragment.TAG
//                                            )
//                                        }
//                                } else {
//                                    childFragmentManager.findFragmentByTag(BlockListFragment.TAG)
//                                }
//
//                            }

                            R.id.settingsFragment -> {
                                if (!childFragmentManager.hasCurrentFragment(SettingsFragment.TAG)) {
                                    SettingsFragment.newInstance()
                                        .apply {
                                            add(
                                                R.id.tab_container,
                                                this,
                                                SettingsFragment.TAG
                                            )
                                        }
                                } else {
                                    childFragmentManager.findFragmentByTag(SettingsFragment.TAG)
                                }
                            }

                            else -> {
                                commitNow()
                                return@setOnNavigationItemSelectedListener false
                            }
                        }
                        replaceFragment(requireNotNull(newFragment))
                        return@setOnNavigationItemSelectedListener true
                    }
                }
                currentTab = item.itemId
                false
            }
        }
    }

    override fun initListeners() {

    }

    override fun collectFlows() {
        with(viewModel) {
            navigateToDestination.subscribe(viewLifecycleOwner) { destination ->
                destination?.let { popUpToFragment(it, null, R.id.tabFragment) }
            }
        }
    }

    override fun onSuccess() {

    }

    override fun onLoading() {

    }

    override fun onError(message: String, code: Int) {

    }

    private fun FragmentManager.hasCurrentFragment(tag: String): Boolean =
        findFragmentByTag(tag) != null

    private fun FragmentTransaction.replaceFragment(newFragment: Fragment) {
        currentFragment?.also { hide(it) }
        newFragment.also { show(it) }
        currentFragment = newFragment
        commitNow()
    }

    private fun updateBottomInsets(bottomInset: Int? = null) {
        requireActivity().supportFragmentManager.setFragmentResult(
            BOTTOM_BAR_INSETS_KEY,
            bundleOf(SCREEN_BOTTOM_INSET_KEY to bottomInset)
        )
    }
}