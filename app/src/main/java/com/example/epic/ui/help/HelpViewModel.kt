package com.example.epic.ui.help

import com.example.epic.sharedPrefs.permissionPrefs.PermissionsPreferences
import com.example.epic.ui.base.viewmodel.BaseViewModel

class HelpViewModel(private val permissionsPreferences: PermissionsPreferences): BaseViewModel() {
    fun canShowNotificationPermission() = permissionsPreferences.notificationRequestCount < 2
    fun incrementNotificationRequestCount() {
        permissionsPreferences.notificationRequestCount++
    }

}