package com.dimitriskatsikas.signing.ui.signing.mappers

import com.dimitriskatsikas.signing.domain.SigningMethod
import com.dimitriskatsikas.signing.domain.SigningMethodType
import com.dimitriskatsikas.signing.ui.signing.SigningView
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class MappersTest {

    @Test
    fun `when mapping SigningMethodType to UI type, then matches expected SigningView type`() {
        assertEquals(SigningView.SigningMethodType.PASSKEY, SigningMethodType.PASSKEY.toUiType())
        assertEquals(SigningView.SigningMethodType.OTP, SigningMethodType.OTP.toUiType())
        assertEquals(SigningView.SigningMethodType.EOA, SigningMethodType.EOA.toUiType())
    }

    @Test
    fun `given List of SigningMethod, when mapping to signing mechanisms, then matches expected UI mechanisms`() {
        val methods = listOf(
            FakeSigningMethod(SigningMethodType.PASSKEY),
            FakeSigningMethod(SigningMethodType.OTP),
            FakeSigningMethod(SigningMethodType.EOA)
        )

        val mechanisms = methods.toSigningMechanism()

        assertEquals(3, mechanisms.size)
        assertEquals(SigningView.SigningMethodType.PASSKEY, mechanisms[0].type)
        assertEquals(SigningView.SigningMethodType.OTP, mechanisms[1].type)
        assertEquals(SigningView.SigningMethodType.EOA, mechanisms[2].type)
    }

    private class FakeSigningMethod(override val type: SigningMethodType) : SigningMethod {
        override suspend fun sign(challenge: String): Result<String> {
            return Result.success("signature")
        }
    }
}
