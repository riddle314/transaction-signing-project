package com.dimitriskatsikas.navigation

import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    data object Withdrawal : Route

    @Serializable
    data class Signing(val operationType: OperationType, val challenge: String) : Route
}
