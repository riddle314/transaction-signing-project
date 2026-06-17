package com.dimitriskatsikas.signing.data

import com.dimitriskatsikas.signing.domain.SigningMethod
import com.dimitriskatsikas.signing.domain.SigningMethodType
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

internal class OtpSigningMethod : SigningMethod {
    override val type: SigningMethodType = SigningMethodType.OTP

    override suspend fun sign(challenge: String): Result<String> {
        delay(1500.milliseconds)
        return Result.success("mock_otp_signature_for_$challenge")
    }
}
