package com.dimitriskatsikas.signing.ui.signing

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimitriskatsikas.common.dispatchers.AppDispatchers
import com.dimitriskatsikas.signing.domain.SigningMethod
import com.dimitriskatsikas.signing.domain.SigningMethodsRepository
import com.dimitriskatsikas.signing.ui.signing.SigningView.OperationType
import com.dimitriskatsikas.signing.ui.signing.SigningView.SigningMechanism
import com.dimitriskatsikas.signing.ui.signing.SigningView.SigningResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val OPERATION_TYPE = "operationType"
private const val CHALLENGE = "challenge"

@HiltViewModel
class SigningViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val signingMechanismsRepository: SigningMethodsRepository,
    private val appDispatchers: AppDispatchers
) : ViewModel() {

    private val operationType: OperationType = OperationType.valueOf(
        checkNotNull(savedStateHandle[OPERATION_TYPE])
    )
    private val challenge: String = checkNotNull(savedStateHandle[CHALLENGE])
    private var domainMethods: List<SigningMethod> = emptyList()

    private val _state = MutableStateFlow<SigningView.State>(SigningView.State.Loading)
    val state = _state.asStateFlow()

    private val _effect: Channel<SigningView.Effect> = Channel(Channel.CONFLATED)
    val effect: Flow<SigningView.Effect> = _effect.receiveAsFlow()

    init {
        loadSigningOptions()
    }

    private fun loadSigningOptions() {
        viewModelScope.launch(appDispatchers.io) {
            _state.value = SigningView.State.Loading
            signingMechanismsRepository.getSigningMethods().onSuccess { methods ->
                domainMethods = methods
                val signingMechanisms = methods.map { method ->
                    SigningMechanism(type = method.type)
                }
                _state.value = SigningView.State.Content(
                    operationType = operationType,
                    signingMechanisms = signingMechanisms
                )
            }.onFailure {
                _state.value = SigningView.State.Error
            }
        }
    }

    fun onUiAction(action: SigningView.UiAction) {
        when (action) {
            is SigningView.UiAction.SignTransaction -> signTransaction(action.signingMechanism)
            SigningView.UiAction.BackPress -> {
                val currentState = _state.value
                if (currentState is SigningView.State.Content) {
                    cancelSigning()
                }
            }

            SigningView.UiAction.RetryLoading -> loadSigningOptions()
        }
    }

    private fun signTransaction(mechanism: SigningMechanism) {
        val currentState = _state.value
        if (currentState is SigningView.State.Content) {
            viewModelScope.launch(appDispatchers.io) {
                _state.value = SigningView.State.SigningLoading(signingMechanism = mechanism)
                val selectedMethod = domainMethods.firstOrNull { it.type == mechanism.type }
                if (selectedMethod != null) {
                    val result = selectedMethod.sign(challenge)
                    result.onSuccess {
                        _effect.send(SigningView.Effect.NavigateBackWithResult(SigningResult.SUCCESS))
                    }.onFailure {
                        _state.value = currentState
                        signingFailed()
                    }
                } else {
                    _state.value = currentState
                    signingFailed()
                }
            }
        }
    }

    private fun cancelSigning() {
        viewModelScope.launch {
            _effect.send(SigningView.Effect.NavigateBackWithResult(SigningResult.CANCELED))
        }
    }

    private fun signingFailed() {
        viewModelScope.launch {
            _effect.send(SigningView.Effect.NavigateBackWithResult(SigningResult.FAILED))
        }
    }
}
