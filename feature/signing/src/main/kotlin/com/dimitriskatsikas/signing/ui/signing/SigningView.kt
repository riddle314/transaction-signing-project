package com.dimitriskatsikas.signing.ui.signing

class SigningView {

    sealed interface State {
        data class Content(
            val operationType: OperationType,
            val signingMechanisms: List<SigningMechanism>
        ) : State

        data object Loading : State
        data object Error: State
        data class SigningLoading(val signingMechanism: SigningMechanism) : State
    }

    sealed interface Effect {
        data class NavigateBackWithResult(val result: SigningResult) : Effect
    }

    sealed interface UiAction {
        data class SignTransaction(val signingMechanism: SigningMechanism) : UiAction
        data object BackPress : UiAction
        data object RetryLoading: UiAction
    }

    enum class OperationType {
        WITHDRAWAL,
        TRANSFER,
        SWAP
    }

    data class SigningMechanism(
        val type: String
    )

    enum class SigningResult {
        SUCCESS,
        CANCELED,
        FAILED
    }
}
