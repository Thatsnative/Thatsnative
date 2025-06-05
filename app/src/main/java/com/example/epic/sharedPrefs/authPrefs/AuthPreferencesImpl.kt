package com.example.epic.sharedPrefs.authPrefs

import com.example.epic.sharedPrefs.base.PreferencesManager
import java.util.Date

private const val PREF_TOKEN = "PREF_TOKEN"
private const val PREF_REFRESH_TOKEN = "PREF_REFRESH_TOKEN"
private const val PREF_USER_ID = "PREF_USER_ID"
private const val PREF_PROFILE_ID = "PREF_PROFILE_ID"
private const val PREF_LOGIN = "PREF_LOGIN"
private const val PREF_EMAIL = "PREF_EMAIL"

class AuthPreferencesImpl(private val manager: PreferencesManager) : AuthPreferences {
    override var login: String
        get() = manager.getString(PREF_LOGIN)
        set(value) {
            manager.put(PREF_LOGIN, value)
        }

    override fun saveToken(token: String, refreshToken: String) {
        manager.put(PREF_TOKEN, token)
        manager.put(PREF_REFRESH_TOKEN, refreshToken)
    }

    override fun saveUserId(id: Int) {
        manager.put(PREF_USER_ID, id)
    }

    override fun saveProfileId(id: String) {
        manager.put(PREF_PROFILE_ID, id)
    }

    override fun saveEmail(email: String) {
        manager.put(PREF_EMAIL, email)
    }

    override fun getUserId() = manager.getInt(PREF_USER_ID)

    override fun getEmail() = manager.getString(PREF_EMAIL)

    override fun getProfileId() = manager.getString(PREF_PROFILE_ID)

    override fun getToken(): String {
        return manager.getString(PREF_TOKEN)
    }

    override fun getRefreshToken() = manager.getString(PREF_REFRESH_TOKEN)

    override fun clearToken() {
        manager.remove(PREF_TOKEN)
        manager.remove(PREF_REFRESH_TOKEN)
    }

    override fun clearUserId() {
        manager.remove(PREF_USER_ID)
    }

    override fun clearProfileId() {
        manager.remove(PREF_PROFILE_ID)
    }

    override fun clearEmail() {
        manager.remove(PREF_EMAIL)
    }

    private fun Long.isExpired() = getCurrentSeconds() >= this && this != 0L

    private fun getCurrentSeconds() = Date().time / 1000
}