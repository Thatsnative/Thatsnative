package com.example.epic.coroutine

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

fun <T> Flow<T>.subscribe(
    owner: LifecycleOwner,
    action: (value: T) -> Unit
) = subscribe(owner.lifecycleScope, action)

fun <T> Flow<T>.subscribeWhenResumed(
    owner: LifecycleOwner,
    action: (value: T) -> Unit
) = subscribeWhenResumed(owner.lifecycleScope, action)

fun <T> Flow<T>.subscribeWhenStarted(
    owner: LifecycleOwner,
    action: (value: T) -> Unit
) = subscribeWhenStarted(owner.lifecycleScope, action)

fun <T> Flow<T>.subscribe(
    scope: LifecycleCoroutineScope,
    action: (value: T) -> Unit
) = scope.launchWhenCreated { collect { action.invoke(it) } }

fun <T> Flow<T>.subscribeWhenResumed(
    scope: LifecycleCoroutineScope,
    action: (value: T) -> Unit
) = scope.launchWhenResumed { collect { action.invoke(it) } }

fun <T> Flow<T>.subscribeWhenStarted(
    scope: LifecycleCoroutineScope,
    action: (value: T) -> Unit
) = scope.launchWhenStarted { collect { action.invoke(it) } }

@Suppress("FunctionName")
fun <T> SingleReplaySharedFlow(
    extraBufferCapacity: Int = 0,
    onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND
) = MutableSharedFlow<T>(1, extraBufferCapacity, onBufferOverflow)

fun <T> MutableSharedFlow<T>.tryEmitScope(
    scope: CoroutineScope,
    value: T
) = scope.launch { emit(value) }
