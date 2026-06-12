package com.dimitriskatsikas.withdrawal.ui.withdrawal

class WithdrawalView {
    data class State(
        val amount: String = "",
        val isProcessing: Boolean = false
    )

    sealed interface Effect {
        data object NavigateToSigning : Effect
        data object ShowErrorToast : Effect
    }

    sealed interface UiAction {
        data class AmountChanged(val amount: String) : UiAction
        data object SubmitWithdrawal : UiAction
    }
}
