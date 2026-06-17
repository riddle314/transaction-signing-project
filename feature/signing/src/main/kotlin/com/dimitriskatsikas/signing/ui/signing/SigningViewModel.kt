package com.dimitriskatsikas.signing.ui.signing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimitriskatsikas.common.dispatchers.AppDispatchers
import com.dimitriskatsikas.navigation.OperationType
import com.dimitriskatsikas.navigation.Route
import com.dimitriskatsikas.signing.domain.SigningCoordinator
import com.dimitriskatsikas.signing.domain.SigningMethod
import com.dimitriskatsikas.signing.domain.SigningMethodsRepository
import com.dimitriskatsikas.signing.domain.SigningResult
import com.dimitriskatsikas.signing.ui.signing.SigningView.SigningMechanism
import com.dimitriskatsikas.signing.ui.signing.mappers.toSigningMechanism
import com.dimitriskatsikas.signing.ui.signing.mappers.toUiType
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = SigningViewModel.Factory::class)
internal class SigningViewModel @AssistedInject constructor(
    @Assisted private val route: Route.Signing,
    private val signingMechanismsRepository: SigningMethodsRepository,
    private val signingCoordinator: SigningCoordinator,
    private val appDispatchers: AppDispatchers
) : ViewModel() {

    private val operationType: OperationType = route.operationType
    private val challenge: String = route.challenge
    private var signingMethods: List<SigningMethod> = emptyList()

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
                signingMethods = methods
                _state.value = SigningView.State.Content(
                    operationType = operationType,
                    signingMechanisms = methods.toSigningMechanism().toImmutableList()
                )
            }.onFailure {
                _state.value = SigningView.State.Error
            }
        }
    }

    fun onUiAction(action: SigningView.UiAction) {
        when (action) {
            is SigningView.UiAction.SignTransaction -> signTransaction(action.signingMechanism)
            SigningView.UiAction.RetryLoading -> loadSigningOptions()
            SigningView.UiAction.BackPress -> {
                val currentState = _state.value
                if (currentState is SigningView.State.Content) {
                    signingCanceled()
                }
            }
        }
    }

    private fun signTransaction(mechanism: SigningMechanism) {
        val currentState = _state.value
        if (currentState is SigningView.State.Content) {
            viewModelScope.launch(appDispatchers.io) {
                _state.value = SigningView.State.SigningLoading(signingMechanism = mechanism)

                val selectedMethod = signingMethods.firstOrNull { it.type.toUiType() == mechanism.type }
                if (selectedMethod != null) {
                    val result = selectedMethod.sign(challenge)
                    result.onSuccess { signature ->
                        signingSucceeded(signature)
                    }.onFailure {
                        signingFailed()
                    }
                } else {
                    signingFailed()
                }
            }
        }
    }

    private fun signingSucceeded(signature: String) {
        viewModelScope.launch(appDispatchers.main) {
            signingCoordinator.sendResult(
                challenge = challenge,
                result = SigningResult.Success(signature)
            )
            _effect.send(SigningView.Effect.NavigateBack)
        }
    }

    private fun signingCanceled() {
        viewModelScope.launch(appDispatchers.main) {
            signingCoordinator.sendResult(
                challenge = challenge,
                result = SigningResult.Canceled
            )
            _effect.send(SigningView.Effect.NavigateBack)
        }
    }

    private fun signingFailed() {
        viewModelScope.launch(appDispatchers.main) {
            signingCoordinator.sendResult(
                challenge = challenge,
                result = SigningResult.Failed
            )
            _effect.send(SigningView.Effect.NavigateBack)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(route: Route.Signing): SigningViewModel
    }
}
