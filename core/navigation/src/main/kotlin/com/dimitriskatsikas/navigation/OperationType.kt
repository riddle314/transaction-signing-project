package com.dimitriskatsikas.navigation

import kotlinx.serialization.Serializable

@Serializable
enum class OperationType {
    WITHDRAWAL,
    TRANSFER,
    SWAP
}
