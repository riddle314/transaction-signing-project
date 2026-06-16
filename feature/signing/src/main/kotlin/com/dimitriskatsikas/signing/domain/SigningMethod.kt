package com.dimitriskatsikas.signing.domain

interface SigningMethod {

    val type: SigningMethodType

    suspend fun sign(challenge: String): Result<String>
}

enum class SigningMethodType {
    PASSKEY,
    OTP,
    EOA
}
