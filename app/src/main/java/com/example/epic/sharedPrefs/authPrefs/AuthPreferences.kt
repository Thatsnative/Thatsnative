package com.example.epic.sharedPrefs.authPrefs

interface AuthPreferences {
    var login : String

    fun saveEmail(email: String)
    fun saveToken(token: String, refreshToken: String)
    fun saveUserId(id: Int)
    fun saveProfileId(id: String)
    fun getUserId(): Int
    fun getEmail(): String
    fun getProfileId(): String
    fun getToken(): String
    fun getRefreshToken(): String
    fun clearToken()
    fun clearUserId()
    fun clearProfileId()
    fun clearEmail()
}
