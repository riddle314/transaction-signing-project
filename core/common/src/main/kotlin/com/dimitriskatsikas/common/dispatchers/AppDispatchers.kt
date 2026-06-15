package com.dimitriskatsikas.common.dispatchers

import kotlinx.coroutines.CoroutineDispatcher

interface AppDispatchers {

    val io: CoroutineDispatcher

    val default: CoroutineDispatcher

    val main: CoroutineDispatcher
}
