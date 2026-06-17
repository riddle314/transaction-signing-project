package com.dimitriskatsikas.signing.ui.signing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dimitriskatsikas.navigation.Route
import com.dimitriskatsikas.signing.ui.signing.components.SigningContent

@Composable
fun SigningScreen(
    route: Route.Signing,
    backStack: SnapshotStateList<Route>
) {
    val viewModel: SigningViewModel = hiltViewModel { factory: SigningViewModel.Factory ->
        factory.create(route)
    }
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
        SigningView.Effect.NavigateBack -> backStack.removeLastOrNull()
    }
}


