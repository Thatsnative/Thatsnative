package com.example.epic.sharedPrefs.permissionPrefs

import com.example.epic.sharedPrefs.base.PreferencesManager

private const val PREF_NOTIFICATION_REQUEST_COUNT = "PREF_NOTIFICATION_REQUEST_COUNT"

class PermissionsPreferencesImpl(private val manager: PreferencesManager) : PermissionsPreferences {
    override var notificationRequestCount: Int
        get() = manager.getInt(PREF_NOTIFICATION_REQUEST_COUNT)
        set(value) {
            manager.put(PREF_NOTIFICATION_REQUEST_COUNT, value)
        }
}