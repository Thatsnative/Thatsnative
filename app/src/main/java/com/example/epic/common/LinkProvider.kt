package com.example.epic.common

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

const val TERMS = "https://avlyx.com/terms/"
const val SUPPORT = "https://growepic-support.freshdesk.com/support/home"
const val PRIVACY_POLICY = "https://avlix.com/privacy/"

fun Context.openExternalUrl(url: String): Boolean {
    val uri = try {
        url.toUri()
    } catch (e: Exception) {
        null
    }

    if (uri == null) {
        return DEFAULT_BOOLEAN
    }

    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        startActivity(intent)
        return true
    } catch (e: ActivityNotFoundException) {
        // handle silently
    }

    try {
        val chooser = Intent.createChooser(intent, "Открыть ссылку через...")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(chooser)
        return true
    } catch (e: ActivityNotFoundException) {
        // handle silently
    }

    return DEFAULT_BOOLEAN
}