package com.redcom1988.core.util

import kotlinx.coroutines.flow.StateFlow

inline fun <T> cachedFlow(
    cache: MutableMap<String, StateFlow<T>>,
    key: String,
    crossinline creator: () -> StateFlow<T>
): StateFlow<T> = cache.getOrPut(key, creator)