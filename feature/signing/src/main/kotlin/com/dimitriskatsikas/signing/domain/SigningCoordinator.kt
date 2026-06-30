package com.dimitriskatsikas.signing.domain

import com.dimitriskatsikas.navigation.OperationType
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SigningCoordinator @Inject constructor() {
    private val _navigationRequests: Channel<NavigationRequest> = Channel(Channel.CONFLATED)
    val navigationRequests: Flow<NavigationRequest> = _navigationRequests.receiveAsFlow()

    private var activeDeferred: CompletableDeferred<SigningResult>? = null
    private var activeChallenge: String? = null

    suspend fun awaitResult(
        challenge: String,
        operationType: OperationType
    ): SigningResult {
        // Send navigation event 
        _navigationRequests.send(
            NavigationRequest(
                challenge = challenge,
                operationType = operationType
            )
        )

        // Wait for the signing screen to complete
        val deferred = CompletableDeferred<SigningResult>()
        activeDeferred = deferred
        activeChallenge = challenge
        return try {
            deferred.await()
        } finally {
            // Cleanup state once we are done waiting
            if (activeChallenge == challenge) {
                activeDeferred = null
                activeChallenge = null
            }
        }
    }

    fun sendResult(challenge: String, result: SigningResult) {
        if (activeChallenge == challenge) {
            activeDeferred?.complete(result)
        }
    }
}

sealed interface SigningResult {
    data class Success(val signature: String) : SigningResult
    data object Canceled : SigningResult
    data object Failed : SigningResult
}

data class NavigationRequest(
    val challenge: String,
    val operationType: OperationType
)
