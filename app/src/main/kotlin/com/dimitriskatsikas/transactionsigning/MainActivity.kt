package com.dimitriskatsikas.transactionsigning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.dimitriskatsikas.navigation.Route
import com.dimitriskatsikas.signing.domain.SigningCoordinator
import com.dimitriskatsikas.transactionsigning.core.designsystem.theme.TransactionSigningTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    private lateinit var signingCoordinator: SigningCoordinator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val backStack = remember { mutableStateListOf<Route>(Route.Withdrawal) }

            LaunchedEffect(Unit) {
                signingCoordinator.navigationRequests.collect { navigationRequest ->
                    backStack.add(
                        Route.Signing(
                            operationType = navigationRequest.operationType,
                            challenge = navigationRequest.challenge
                        )
                    )
                }
            }

            TransactionSigningTheme {
                AppNavigation(backStack)
            }
        }
    }
}
