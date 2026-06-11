package com.dimitriskatsikas.transactionsigning

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.dimitriskatsikas.navigation.Route
import com.dimitriskatsikas.transactionsigning.feature.withdrawal.ui.withdrawal.WithdrawalScreen
import com.dimitriskatsikas.transactionsigning.feature.signing.ui.signing.SigningScreen

@Composable
fun AppNavigation() {
    val backStack = remember { mutableStateListOf<Route>(Route.Withdrawal) }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Route.Withdrawal> {
                WithdrawalScreen(
                    viewModel = hiltViewModel(),
                    backStack = backStack
                )
            }
            entry<Route.Signing> {
                SigningScreen(
                    viewModel = hiltViewModel(),
                    backStack = backStack
                )
            }
        }
    )
}
