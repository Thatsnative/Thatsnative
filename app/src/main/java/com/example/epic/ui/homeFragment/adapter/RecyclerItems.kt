package com.example.epic.ui.homeFragment.adapter

import com.example.epic.R
import com.example.epic.sharedPrefs.mainFeaturesPrefs.MainFeaturesPreferences

fun getEnhanceYourProtectionItems(mainFeaturesPreferences: MainFeaturesPreferences) = listOf(
    HomeFeatureItemModel(
        id = 0,
        icon = R.drawable.ic_youtube_40,
        title = R.string.youtube_protection,
        description = R.string.block_video_ads,
        isChecked = mainFeaturesPreferences.isYoutubeProtectionEnabled
    ),
    HomeFeatureItemModel(
        id = 1,
        icon = R.drawable.ic_speed_40,
        title = R.string.speed,
        description = R.string.turbo_charge_loading_speeds,
        isChecked = mainFeaturesPreferences.isSpeedChargeEnabled
    ),
    HomeFeatureItemModel(
        id = 2,
        icon = R.drawable.ic_privacy_40,
        title = R.string.privacy,
        description = R.string.prevent_tracking_online,
        isChecked = mainFeaturesPreferences.isPrivacyProtectionEnabled
    ),
    HomeFeatureItemModel(
        id = 3,
        icon = R.drawable.ic_malware_40,
        title = R.string.security,
        description = R.string.malware_scam_site_protection,
        isChecked = mainFeaturesPreferences.isMalwareProtectionEnabled
    ),
)

fun getExploreFeaturesItems(mainFeaturesPreferences: MainFeaturesPreferences) = listOf(
    HomeFeatureItemModel(
        id = 0,
        icon = R.drawable.ic_cookie_40,
        title = R.string.cookie_protection,
        description = R.string.block_tracking_cookies,
        isChecked = mainFeaturesPreferences.isCookieProtectionEnabled
    ),
    HomeFeatureItemModel(
        id = 1,
        icon = R.drawable.ic_social_media_40,
        title = R.string.social_media_widgets,
        description = R.string.block_social_media_tracking,
        isChecked = mainFeaturesPreferences.isSocialMediaProtectionEnabled
    ),
)