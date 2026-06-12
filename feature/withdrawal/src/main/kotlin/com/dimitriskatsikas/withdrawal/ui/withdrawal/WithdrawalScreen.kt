package com.dimitriskatsikas.withdrawal.ui.withdrawal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dimitriskatsikas.navigation.Route
import com.dimitriskatsikas.withdrawal.ui.withdrawal.components.WithdrawalContent

@Composable
fun WithdrawalScreen(
    viewModel: WithdrawalViewModel,
    backStack: SnapshotStateList<Route>
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    WithdrawalContent(
        state = state,
        onUiAction = viewModel::onUiAction
    )

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            handleEffect(
                effect = effect,
                backStack = backStack
            )
        }
    }
}

private fun handleEffect(
    effect: WithdrawalView.Effect,
    backStack: SnapshotStateList<Route>
) {
    when (effect) {
        WithdrawalView.Effect.NavigateToSigning -> TODO()
        WithdrawalView.Effect.ShowErrorToast -> TODO()
    }
}
