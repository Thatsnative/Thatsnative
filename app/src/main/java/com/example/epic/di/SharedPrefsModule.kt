package com.example.epic.di

import com.example.epic.sharedPrefs.authPrefs.AuthPreferences
import com.example.epic.sharedPrefs.authPrefs.AuthPreferencesImpl
import com.example.epic.sharedPrefs.mainFeaturesPrefs.MainFeaturesPreferences
import com.example.epic.sharedPrefs.mainFeaturesPrefs.MainFeaturesPreferencesImpl
import com.example.epic.sharedPrefs.base.PreferencesManager
import com.example.epic.sharedPrefs.base.SharedPreferencesManagerImpl
import com.example.epic.sharedPrefs.onboardingPrefs.OnboardingPreferences
import com.example.epic.sharedPrefs.onboardingPrefs.OnboardingPreferencesImpl
import com.example.epic.sharedPrefs.permissionPrefs.PermissionsPreferences
import com.example.epic.sharedPrefs.permissionPrefs.PermissionsPreferencesImpl
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.bind
import org.koin.dsl.module

val sharedPrefsModule = module {
    singleOf(::SharedPreferencesManagerImpl) { bind<PreferencesManager>() }
    singleOf(::AuthPreferencesImpl) { bind<AuthPreferences>() }
    singleOf(::MainFeaturesPreferencesImpl) { bind<MainFeaturesPreferences>() }
    singleOf(::OnboardingPreferencesImpl) { bind<OnboardingPreferences>() }
    singleOf(::PermissionsPreferencesImpl) { bind<PermissionsPreferences>() }
}