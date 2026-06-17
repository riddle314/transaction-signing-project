package com.dimitriskatsikas.signing.domain

import kotlinx.coroutines.CompletableDeferred
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SigningCoordinator @Inject constructor() {
    private var activeDeferred: CompletableDeferred<SigningResult>? = null
    private var activeChallenge: String? = null

    suspend fun awaitResult(challenge: String): SigningResult {
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
