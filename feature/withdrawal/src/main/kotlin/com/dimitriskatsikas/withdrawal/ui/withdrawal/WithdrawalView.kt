package com.dimitriskatsikas.withdrawal.ui.withdrawal

internal object WithdrawalView {

    sealed interface State {
        data class Content(
            val amount: String = "",
            val ctaState: CtaState = CtaState.Disabled
        ) : State

        data object Success : State
    }

    sealed interface CtaState {
        data object Enabled : CtaState
        data object Disabled : CtaState
        data object Loading : CtaState
    }

    sealed interface Effect {
        data class ShowErrorToast(val errorType: ErrorType) : Effect
    }

    sealed interface ErrorType {
        data object TransactionCanceled : ErrorType
        data object TransactionFailed : ErrorType
    }

    sealed interface UiAction {
        data class AmountChanged(val amount: String) : UiAction
        data object SubmitWithdrawal : UiAction
    }
}
