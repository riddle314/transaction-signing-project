package com.dimitriskatsikas.signing.domain

interface SigningMethod {

    val type: String

    suspend fun sign(challenge: String): Result<String>
}
