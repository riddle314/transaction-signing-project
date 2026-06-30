package com.dimitriskatsikas.transactionsigning

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.dimitriskatsikas.navigation.Route
import com.dimitriskatsikas.signing.ui.signing.SigningScreen
import com.dimitriskatsikas.withdrawal.ui.withdrawal.WithdrawalScreen

@Composable
fun AppNavigation(backStack: SnapshotStateList<Route>) {
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Route.Withdrawal> {
                WithdrawalScreen()
            }
            entry<Route.Signing> { route ->
                SigningScreen(
                    route = route,
                    backStack = backStack
                )
            }
        }
    )
}
