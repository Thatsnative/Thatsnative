package com.example.epic.common

import org.koin.java.KoinJavaComponent.inject
inline fun <reified T> inject(): Lazy<T> { return inject(T::class.java) }