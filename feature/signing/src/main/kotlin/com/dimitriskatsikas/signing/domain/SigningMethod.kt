package com.dimitriskatsikas.signing.domain

internal interface SigningMethod {

    val type: SigningMethodType

    suspend fun sign(challenge: String): Result<String>
}

internal enum class SigningMethodType {
    PASSKEY,
    OTP,
    EOA
}
