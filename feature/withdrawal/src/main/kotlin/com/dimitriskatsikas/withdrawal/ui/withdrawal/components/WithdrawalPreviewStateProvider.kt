package com.dimitriskatsikas.withdrawal.ui.withdrawal.components

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.dimitriskatsikas.withdrawal.ui.withdrawal.WithdrawalView

class WithdrawalPreviewStateProvider : PreviewParameterProvider<WithdrawalView.State> {

    override val values = sequenceOf(
        WithdrawalView.State.Content(
            amount = "",
            ctaState = WithdrawalView.CtaState.Disabled
        ),
        WithdrawalView.State.Content(
            amount = "100",
            ctaState = WithdrawalView.CtaState.Enabled
        ),
        WithdrawalView.State.Content(
            amount = "100",
            ctaState = WithdrawalView.CtaState.Loading
        ),
        WithdrawalView.State.Success
    )
}
