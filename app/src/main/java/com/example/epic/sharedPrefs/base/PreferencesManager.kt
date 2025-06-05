package com.example.epic.sharedPrefs.base

import android.content.SharedPreferences
import com.example.epic.common.DEFAULT_INT
import com.example.epic.common.EMPTY_STRING

interface PreferencesManager {
    fun put(key: String, value: String)
    fun put(key: String, value: Int)
    fun put(key: String, value: Long)
    fun put(key: String, value: Boolean)
    fun put(key: String, value: Float)
    fun remove(key: String)
    fun getString(key: String): String
    fun getString(key: String, defValue: String = EMPTY_STRING): String
    fun getInt(key: String, defValue: Int = DEFAULT_INT): Int
    fun getLong(key: String): Long
    fun getBoolean(key: String): Boolean
    fun getBoolean(key: String, defValue: Boolean): Boolean
    fun getFloat(key: String): Float
    fun hasValue(key: String): Boolean

    fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener)
    fun unregisterListener(listener: SharedPreferences.OnSharedPreferenceChangeListener)
}