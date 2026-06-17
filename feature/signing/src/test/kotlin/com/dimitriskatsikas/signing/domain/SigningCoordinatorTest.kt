package com.dimitriskatsikas.signing.domain

import kotlinx.coroutines.async
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

internal class SigningCoordinatorTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testedClass = SigningCoordinator()

    @Test
    fun `given active challenge, when sending correct signature, then awaitResult returns success`() =
        runTest(testDispatcher) {
            val challenge = "test_challenge"
            val expectedResult = SigningResult.Success("test_signature")

            // Part 1 initiate awaitResult
            // UnconfinedTestDispatcher runs the async block eagerly until it suspends,
            // so coordinator.awaitResult has executed and is waiting on deferred.await() inside the coordinator.
            val deferredResult = async {
                testedClass.awaitResult(challenge)
            }

            assertFalse(deferredResult.isCompleted)

            // Part 2 send correct result
            testedClass.sendResult(challenge, expectedResult)

            // Part 3 receive the result and check
            val actualResult = deferredResult.await()
            assertEquals(expectedResult, actualResult)
        }

    @Test
    fun `given active challenge, when sending mismatching challenge result, then active deferred is not completed`() =
        runTest(testDispatcher) {
            val challenge = "active_challenge"
            val wrongChallenge = "wrong_challenge"

            // UnconfinedTestDispatcher runs the async block eagerly until it suspends,
            // so coordinator.awaitResult has executed and is waiting on deferred.await() inside the coordinator.
            val deferredResult = async {
                testedClass.awaitResult(challenge)
            }

            // Should be active/suspended
            assertFalse(deferredResult.isCompleted)

            // Send result to the wrong challenge
            testedClass.sendResult(wrongChallenge, SigningResult.Success("sig"))

            // Should still be active/suspended
            assertFalse(deferredResult.isCompleted)

            // Send correct result to complete the test
            testedClass.sendResult(challenge, SigningResult.Canceled)
            val actualResult = deferredResult.await()
            assertEquals(SigningResult.Canceled, actualResult)
        }
}
