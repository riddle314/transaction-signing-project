package com.dimitriskatsikas.signing.ui.signing

class SigningView {

    sealed interface State {
        data class Content(
            val operationType: OperationType,
            val signingMechanisms: List<SigningMechanism>
        ) : State

        data object Loading : State
    }

    sealed interface Effect {
        data class NavigateBackWithResult(val result: SigningResult) : Effect
    }

    sealed interface UiAction {
        data class SignTransaction(val signingMechanism: SigningMechanism) : UiAction
        data object Back : UiAction
    }

    enum class OperationType {
        WITHDRAWAL,
        TRANSFER,
        SWAP
    }

    enum class SigningMechanism {
        PASSKEYS,
        OTP,
        EOA
    }

    enum class SigningResult {
        SUCCESS,
        CANCELED,
        FAILED
    }
}
