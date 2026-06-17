package com.dimitriskatsikas.signing.data

import com.dimitriskatsikas.signing.domain.SigningMethod
import com.dimitriskatsikas.signing.domain.SigningMethodType
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

internal class EoaSigningMethod : SigningMethod {
    override val type: SigningMethodType = SigningMethodType.EOA

    override suspend fun sign(challenge: String): Result<String> {
        delay(1500.milliseconds)
        return Result.success("mock_eoa_signature_for_$challenge")
    }
}
