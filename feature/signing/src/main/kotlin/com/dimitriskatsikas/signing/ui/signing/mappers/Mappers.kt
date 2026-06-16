package com.dimitriskatsikas.signing.ui.signing.mappers

import com.dimitriskatsikas.signing.domain.SigningMethod
import com.dimitriskatsikas.signing.domain.SigningMethodType
import com.dimitriskatsikas.signing.ui.signing.SigningView
import com.dimitriskatsikas.signing.ui.signing.SigningView.SigningMechanism

internal fun SigningMethodType.toUiType(): SigningView.SigningMethodType = when (this) {
    SigningMethodType.PASSKEY -> SigningView.SigningMethodType.PASSKEY
    SigningMethodType.OTP -> SigningView.SigningMethodType.OTP
    SigningMethodType.EOA -> SigningView.SigningMethodType.EOA
}

internal fun List<SigningMethod>.toSigningMechanism(): List<SigningMechanism> = map { signingMethod ->
    SigningMechanism(type = signingMethod.type.toUiType())
}
