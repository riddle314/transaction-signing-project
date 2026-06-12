package com.dimitriskatsikas.signing.ui.signing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dimitriskatsikas.navigation.Route
import com.dimitriskatsikas.signing.ui.signing.components.SigningContent

@Composable
fun SigningScreen(
    viewModel: SigningViewModel,
    backStack: SnapshotStateList<Route>
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SigningContent(
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
    effect: SigningView.Effect,
    backStack: SnapshotStateList<Route>
) {
    when (effect) {
        is SigningView.Effect.NavigateBackWithResult -> TODO()
    }
}


