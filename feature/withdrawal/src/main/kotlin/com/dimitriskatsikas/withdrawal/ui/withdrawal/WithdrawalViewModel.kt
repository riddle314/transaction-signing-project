package com.dimitriskatsikas.withdrawal.ui.withdrawal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimitriskatsikas.common.dispatchers.AppDispatchers
import com.dimitriskatsikas.navigation.OperationType
import com.dimitriskatsikas.signing.domain.SigningCoordinator
import com.dimitriskatsikas.signing.domain.SigningResult
import com.dimitriskatsikas.withdrawal.domain.WithdrawalTransactionRepository
import com.dimitriskatsikas.withdrawal.ui.withdrawal.WithdrawalView.CtaState
import com.dimitriskatsikas.withdrawal.ui.withdrawal.WithdrawalView.ErrorType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class WithdrawalViewModel @Inject constructor(
    private val withdrawalTransactionRepository: WithdrawalTransactionRepository,
    private val signingCoordinator: SigningCoordinator,
    private val appDispatchers: AppDispatchers
) : ViewModel() {

    private val _state = MutableStateFlow<WithdrawalView.State>(WithdrawalView.State.Content())
    val state = _state.asStateFlow()

    private val _effect: Channel<WithdrawalView.Effect> = Channel(Channel.CONFLATED)
    val effect: Flow<WithdrawalView.Effect> = _effect.receiveAsFlow()

    fun onUiAction(action: WithdrawalView.UiAction) {
        when (action) {
            is WithdrawalView.UiAction.AmountChanged -> {
                _state.update { state ->
                    if (state is WithdrawalView.State.Content) {
                        state.copy(
                            amount = action.amount,
                            ctaState = if (action.amount.isEmpty()) {
                                CtaState.Disabled
                            } else {
                                CtaState.Enabled
                            }
                        )
                    } else state
                }
            }

            WithdrawalView.UiAction.SubmitWithdrawal -> submitWithdrawal()
        }
    }

    private fun submitWithdrawal() {
        val currentState = _state.value
        if (currentState is WithdrawalView.State.Content && currentState.amount.isNotEmpty()) {
            val amount = currentState.amount
            viewModelScope.launch(appDispatchers.io) {
                _state.value = WithdrawalView.State.Content(
                    amount = amount,
                    ctaState = CtaState.Loading
                )

                withdrawalTransactionRepository.getQuotation(amount)
                    .onSuccess { result ->
                        _effect.send(
                            WithdrawalView.Effect.NavigateToSigning(
                                operationType = OperationType.WITHDRAWAL,
                                challenge = result.challenge
                            )
                        )

                        when (val signingResult = signingCoordinator.awaitResult(result.challenge)) {
                            is SigningResult.Success -> {
                                withdrawalTransactionRepository.submitSignedTransaction(signature = signingResult.signature)
                                    .onSuccess {
                                        _state.value = WithdrawalView.State.Success
                                    }.onFailure {
                                        handleFailure(amount, ErrorType.TransactionFailed)
                                    }
                            }

                            SigningResult.Canceled -> handleFailure(
                                amount = amount,
                                errorType = ErrorType.TransactionCanceled
                            )

                            SigningResult.Failed -> handleFailure(
                                amount = amount,
                                errorType = ErrorType.TransactionFailed
                            )
                        }
                    }.onFailure {
                        handleFailure(
                            amount = amount,
                            errorType = ErrorType.TransactionFailed
                        )
                    }
            }
        }
    }

    private fun handleFailure(
        amount: String,
        errorType: ErrorType
    ) {
        viewModelScope.launch(appDispatchers.main) {
            _effect.send(WithdrawalView.Effect.ShowErrorToast(errorType))
            _state.value = WithdrawalView.State.Content(
                amount = amount,
                ctaState = if (amount.isEmpty()) {
                    CtaState.Disabled
                } else {
                    CtaState.Enabled
                }
            )
        }
    }
}
