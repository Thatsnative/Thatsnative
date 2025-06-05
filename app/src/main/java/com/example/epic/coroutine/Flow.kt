package com.example.epic.coroutine

import kotlinx.coroutines.flow.SharedFlow

val <T> SharedFlow<T>.firstValueCacheOrNull
    get() = replayCache.firstOrNull()

val <T> SharedFlow<T>.firstValueCache
    get() = firstValueCacheOrNull ?: throw IllegalStateException("value == null")

val <T> SharedFlow<T>.lastValueCacheOrNull
    get() = replayCache.lastOrNull()

val <T> SharedFlow<T>.lastValueCache
    get() = lastValueCacheOrNull ?: throw IllegalStateException("value == null")
