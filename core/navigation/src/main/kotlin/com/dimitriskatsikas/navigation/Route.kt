package com.dimitriskatsikas.navigation

import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    data object Withdrawal : Route

    @Serializable
    data object Signing : Route
}
