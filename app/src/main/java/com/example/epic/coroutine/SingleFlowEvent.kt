package com.example.epic.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SingleFlowEvent<T> {
    private val channel = Channel<T?>(Channel.BUFFERED)

    suspend fun tryEmit(value: T?) {
        channel.send(value)
    }

    fun tryEmitScope(scope: CoroutineScope, value: T?) {
        scope.launch { tryEmit(value) }
    }

    suspend fun call() {
        tryEmit(null)
    }

    fun callScope(scope: CoroutineScope) {
        scope.launch { call() }
    }

    fun asFlow() = channel.receiveAsFlow()

    fun asFlowNotNull() = channel.receiveAsFlow()
        .filterNotNull()
}
