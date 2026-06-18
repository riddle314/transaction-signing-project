package com.dimitriskatsikas.signing.ui.signing

import com.dimitriskatsikas.navigation.OperationType
import kotlinx.collections.immutable.ImmutableList

internal object SigningView {

    sealed interface State {
        data class Content(
            val operationType: OperationType,
            val signingMechanisms: ImmutableList<SigningMechanism>
        ) : State

        data object Loading : State
        data object Error : State
        data class SigningLoading(val signingMechanism: SigningMechanism) : State
    }

    sealed interface Effect {
        data object NavigateBack : Effect
    }

    sealed interface UiAction {
        data class SignTransaction(val signingMechanism: SigningMechanism) : UiAction
        data object BackPress : UiAction
        data object RetryLoading : UiAction
    }

    data class SigningMechanism(
        val type: SigningMethodType
    )

    enum class SigningMethodType {
        PASSKEY,
        OTP,
        EOA
    }
}
