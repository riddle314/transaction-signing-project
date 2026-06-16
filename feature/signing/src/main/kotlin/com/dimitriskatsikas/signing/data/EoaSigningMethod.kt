package com.dimitriskatsikas.signing.data

import com.dimitriskatsikas.signing.domain.SigningMethod
import com.dimitriskatsikas.signing.domain.SigningMethodType
import kotlin.time.Duration.Companion.milliseconds

class EoaSigningMethod : SigningMethod {
    override val type: SigningMethodType = SigningMethodType.EOA

    override suspend fun sign(challenge: String): Result<String> {
        kotlinx.coroutines.delay(1500.milliseconds)
        return Result.success("mock_eoa_signature_for_$challenge")
    }
}
