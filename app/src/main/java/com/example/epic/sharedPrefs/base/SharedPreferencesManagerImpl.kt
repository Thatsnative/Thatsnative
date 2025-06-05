package com.example.epic.sharedPrefs.base

import android.content.Context
import android.content.SharedPreferences
import com.example.epic.common.DEFAULT_BOOLEAN
import com.example.epic.common.DEFAULT_FLOAT
import com.example.epic.common.DEFAULT_LONG
import com.example.epic.common.EMPTY_STRING

private const val SHARED_PREFERENCE_FILE_NAME = "shared"

class SharedPreferencesManagerImpl(context: Context) : PreferencesManager {
    private val sharedPrefs = context.getSharedPreferences(
        SHARED_PREFERENCE_FILE_NAME,
        Context.MODE_PRIVATE
    )
    private val editor = sharedPrefs.edit()

    override fun put(key: String, value: String) { editor.putString(key, value).apply() }
    override fun put(key: String, value: Int) { editor.putInt(key, value).apply() }
    override fun put(key: String, value: Long) { editor.putLong(key, value).apply() }
    override fun put(key: String, value: Boolean) { editor.putBoolean(key, value).apply() }
    override fun put(key: String, value: Float) { editor.putFloat(key, value).apply() }
    override fun remove(key: String) { editor.remove(key).apply() }
    override fun getString(key: String) = sharedPrefs.getString(key, EMPTY_STRING).orEmpty()
    override fun getString(key: String, defValue: String) = sharedPrefs.getString(key, defValue).orEmpty()

    override fun getInt(key: String, defValue: Int) = sharedPrefs.getInt(key, defValue)
    override fun getLong(key: String) = sharedPrefs.getLong(key, DEFAULT_LONG)
    override fun getBoolean(key: String) = getBoolean(key, DEFAULT_BOOLEAN)
    override fun getBoolean(key: String, defValue: Boolean) = sharedPrefs.getBoolean(key, defValue)
    override fun getFloat(key: String) = sharedPrefs.getFloat(key, DEFAULT_FLOAT)
    override fun hasValue(key: String) = sharedPrefs.contains(key)

    override fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) =
        sharedPrefs.registerOnSharedPreferenceChangeListener(listener)
    override fun unregisterListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) =
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(listener)
}