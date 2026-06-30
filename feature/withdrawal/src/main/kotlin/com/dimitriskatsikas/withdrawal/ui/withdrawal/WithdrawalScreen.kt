package com.dimitriskatsikas.withdrawal.ui.withdrawal

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dimitriskatsikas.transactionsigning.feature.withdrawal.R
import com.dimitriskatsikas.withdrawal.ui.withdrawal.components.WithdrawalContent

@Composable
fun WithdrawalScreen() {
    val viewModel: WithdrawalViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    WithdrawalContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onUiAction = viewModel::onUiAction
    )

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            handleEffect(
                context = context,
                effect = effect,
                snackbarHostState = snackbarHostState
            )
        }
    }
}

private suspend fun handleEffect(
    context: Context,
    effect: WithdrawalView.Effect,
    snackbarHostState: SnackbarHostState
) {
    when (effect) {
        is WithdrawalView.Effect.ShowErrorToast -> {
            val message = when (effect.errorType) {
                WithdrawalView.ErrorType.TransactionCanceled -> {
                    context.getString(R.string.withdrawal_error_transaction_canceled)
                }

                WithdrawalView.ErrorType.TransactionFailed -> {
                    context.getString(R.string.withdrawal_error_transaction_failed)
                }
            }
            snackbarHostState.showSnackbar(message = message)
        }
    }
}
