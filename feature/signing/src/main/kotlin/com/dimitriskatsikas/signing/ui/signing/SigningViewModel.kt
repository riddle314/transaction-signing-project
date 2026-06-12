package com.dimitriskatsikas.signing.ui.signing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimitriskatsikas.signing.ui.signing.SigningView.OperationType
import com.dimitriskatsikas.signing.ui.signing.SigningView.SigningMechanism
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SigningViewModel @Inject constructor(
    val operationType: OperationType
) : ViewModel() {

    private val signingMechanisms = SigningMechanism.entries
    private val _state = MutableStateFlow(
        SigningView.State.Content(
            operationType = operationType,
            signingMechanisms = signingMechanisms
        )
    )
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SigningView.State.Content(
            operationType = operationType,
            signingMechanisms = signingMechanisms
        )
    )

    private val _effect: Channel<SigningView.Effect> = Channel(Channel.CONFLATED)
    val effect: Flow<SigningView.Effect> = _effect.receiveAsFlow()

    fun onUiAction(action: SigningView.UiAction) {

    }
}
