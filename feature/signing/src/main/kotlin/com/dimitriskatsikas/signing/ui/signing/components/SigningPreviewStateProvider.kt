package com.dimitriskatsikas.signing.ui.signing.components

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.dimitriskatsikas.signing.ui.signing.SigningView
import com.dimitriskatsikas.signing.ui.signing.SigningView.OperationType
import com.dimitriskatsikas.signing.ui.signing.SigningView.SigningMechanism

class SigningPreviewStateProvider : PreviewParameterProvider<SigningView.State> {

    override val values = sequenceOf(
        SigningView.State.Loading,
        SigningView.State.SigningLoading(SigningMechanism(type = SigningView.SigningMethodType.PASSKEY)),
        SigningView.State.Content(
            operationType = OperationType.WITHDRAWAL,
            signingMechanisms = listOf(
                SigningMechanism(type = SigningView.SigningMethodType.PASSKEY),
                SigningMechanism(type = SigningView.SigningMethodType.OTP),
                SigningMechanism(type = SigningView.SigningMethodType.EOA)
            )
        ),
        SigningView.State.Error
    )
}
