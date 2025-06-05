package com.example.epic.coroutine

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

val Context.applicationScope: CoroutineScope
    get() = requireNotNull(applicationContext as? ApplicationScopeProvider).applicationScope

@Suppress("FunctionName")
fun ApplicationScope() = CoroutineScope(SupervisorJob() + Dispatchers.Default)

interface ApplicationScopeProvider {
    val applicationScope: CoroutineScope
}
