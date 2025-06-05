package com.example.epic.analityc

import android.util.Log

object AnalyticLogger {
    private const val TAG = "Analytic Logger"

    fun info(message: String) {
        Log.i(TAG, message)
    }
}