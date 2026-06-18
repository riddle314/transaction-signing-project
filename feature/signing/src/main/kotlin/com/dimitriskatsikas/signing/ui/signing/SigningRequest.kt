package com.dimitriskatsikas.signing.ui.signing

import com.dimitriskatsikas.navigation.OperationType

data class SigningRequest(
    val challenge: String,
    val operationType: OperationType
)
