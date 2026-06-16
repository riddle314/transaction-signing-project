package com.dimitriskatsikas.signing.data

import com.dimitriskatsikas.signing.domain.SigningMethod
import kotlin.time.Duration.Companion.milliseconds

class MockSigningMethod(
    override val type: String
) : SigningMethod {

    override suspend fun sign(challenge: String): Result<String> {
        kotlinx.coroutines.delay(1500.milliseconds)
        return Result.success("mock_${type.lowercase()}_signature_for_$challenge")
    }

}
