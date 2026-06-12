package com.dimitriskatsikas.withdrawal.ui.withdrawal

class WithdrawalView {
    data class State(
        val amount: String = "",
        val ctaState: CtaState = CtaState.Disabled
    )

    sealed interface CtaState {
        data object Enabled : CtaState
        data object Disabled : CtaState
        data object Loading : CtaState
    }

    sealed interface Effect {
        data object NavigateToSigning : Effect
        data object ShowErrorToast : Effect
    }

    sealed interface UiAction {
        data class AmountChanged(val amount: String) : UiAction
        data object SubmitWithdrawal : UiAction
    }
}
