package com.example.epic.di

import com.example.epic.ui.authorization.AuthViewModel
import com.example.epic.ui.authorization.CheckCodeViewModel
import com.example.epic.ui.blockListFragment.BlockListViewModel
import com.example.epic.ui.help.HelpViewModel
import com.example.epic.ui.main.MainViewModel
import com.example.epic.ui.homeFragment.HomeFragmentViewModel
import com.example.epic.ui.onboarding.OnboardingViewModel
import com.example.epic.ui.settingsfragment.SettingsViewModel
import com.example.epic.ui.tabFragment.TabViewModel
import com.example.epic.ui.whiteList.WhiteListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelsModule = module {
    viewModelOf(::AuthViewModel)
    viewModelOf(::CheckCodeViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::HomeFragmentViewModel)
    viewModelOf(::TabViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::BlockListViewModel)
    viewModelOf(::WhiteListViewModel)
    viewModelOf(::HelpViewModel)
}