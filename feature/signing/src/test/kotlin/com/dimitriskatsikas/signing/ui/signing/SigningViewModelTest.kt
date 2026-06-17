package com.dimitriskatsikas.signing.ui.signing

import app.cash.turbine.test
import com.dimitriskatsikas.common.dispatchers.AppDispatchers
import com.dimitriskatsikas.navigation.OperationType
import com.dimitriskatsikas.navigation.Route
import com.dimitriskatsikas.signing.domain.SigningCoordinator
import com.dimitriskatsikas.signing.domain.SigningMethod
import com.dimitriskatsikas.signing.domain.SigningMethodType
import com.dimitriskatsikas.signing.domain.SigningMethodsRepository
import com.dimitriskatsikas.signing.domain.SigningResult
import com.dimitriskatsikas.signing.ui.signing.SigningView.Effect
import com.dimitriskatsikas.signing.ui.signing.SigningView.SigningMechanism
import com.dimitriskatsikas.signing.ui.signing.SigningView.State
import com.dimitriskatsikas.signing.ui.signing.SigningView.UiAction
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import com.dimitriskatsikas.signing.ui.signing.SigningView.SigningMethodType as UiSigningMethodType

@OptIn(ExperimentalCoroutinesApi::class)
internal class SigningViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val appDispatchers = object : AppDispatchers {
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
        override val main: CoroutineDispatcher = testDispatcher
    }

    private val route = Route.Signing(
        operationType = OperationType.WITHDRAWAL,
        challenge = "test_challenge"
    )

    private val signingCoordinator = SigningCoordinator()
    private lateinit var repository: FakeSigningMethodsRepository
    private lateinit var testedClass: SigningViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeSigningMethodsRepository(Result.success(emptyList()))
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() {
        testedClass = SigningViewModel(
            route = route,
            signingMechanismsRepository = repository,
            signingCoordinator = signingCoordinator,
            appDispatchers = appDispatchers
        )
    }

    @Test
    fun `given successful load of signing methods, when ViewModel initializes, then transitions state to Content`() =
        runTest {
            val mockMethods = listOf(
                FakeSigningMethod(
                    type = SigningMethodType.PASSKEY,
                    signResult = Result.success("signature")
                )
            )
            repository.result = Result.success(mockMethods)
            createViewModel()

            testedClass.state.test {
                assertEquals(
                    State.Content(
                        operationType = OperationType.WITHDRAWAL,
                        signingMechanisms = persistentListOf(SigningMechanism(UiSigningMethodType.PASSKEY))
                    ),
                    awaitItem()
                )
            }
        }

    @Test
    fun `given failed load of signing methods, when ViewModel initializes, then transitions state to Error`() =
        runTest {
            repository.result = Result.failure(Exception("Load failed"))
            createViewModel()

            testedClass.state.test {
                assertEquals(State.Error, awaitItem())
            }
        }

    @Test
    fun `given initial state is Error, when RetryLoading action received, then reloads signing options`() =
        runTest {
            // Start with failure
            repository.result = Result.failure(Exception("Load failed"))
            createViewModel()

            testedClass.state.test {
                assertEquals(State.Error, awaitItem())

                // Setup repository to succeed on retry
                repository.result = Result.success(
                    listOf(
                        FakeSigningMethod(
                            type = SigningMethodType.OTP,
                            signResult = Result.success("sig")
                        )
                    )
                )

                testedClass.onUiAction(UiAction.RetryLoading)

                assertEquals(
                    State.Content(
                        operationType = OperationType.WITHDRAWAL,
                        signingMechanisms = persistentListOf(SigningMechanism(UiSigningMethodType.OTP))
                    ),
                    awaitItem()
                )
            }
        }

    @Test
    fun `given successful sign transaction, when SignTransaction action received, then sends success to coordinator and navigates back`() =
        runTest {
            val mockMethod = FakeSigningMethod(
                type = SigningMethodType.PASSKEY,
                signResult = Result.success("mock_signature")
            )
            repository.result = Result.success(listOf(mockMethod))
            createViewModel()

            testedClass.effect.test {
                testedClass.onUiAction(
                    UiAction.SignTransaction(SigningMechanism(UiSigningMethodType.PASSKEY))
                )

                assertEquals(Effect.NavigateBack, awaitItem())
            }

            assertEquals(
                SigningResult.Success("mock_signature"),
                signingCoordinator.awaitResult(route.challenge)
            )
        }

    @Test
    fun `given failed sign transaction, when SignTransaction action received, then sends failed to coordinator and navigates back`() =
        runTest {
            val mockMethod = FakeSigningMethod(
                type = SigningMethodType.PASSKEY,
                signResult = Result.failure(Exception("User cancel/fail"))
            )
            repository.result = Result.success(listOf(mockMethod))
            createViewModel()

            testedClass.effect.test {
                testedClass.onUiAction(
                    UiAction.SignTransaction(SigningMechanism(UiSigningMethodType.PASSKEY))
                )

                assertEquals(Effect.NavigateBack, awaitItem())
            }

            assertEquals(
                SigningResult.Failed,
                signingCoordinator.awaitResult(route.challenge)
            )
        }

    @Test
    fun `when BackPress action received, then sends canceled to coordinator and navigates back`() = runTest {
        repository.result = Result.success(emptyList())
        createViewModel()

        testedClass.effect.test {
            testedClass.onUiAction(UiAction.BackPress)

            assertEquals(Effect.NavigateBack, awaitItem())
        }

        assertEquals(
            SigningResult.Canceled,
            signingCoordinator.awaitResult(route.challenge)
        )
    }

    private class FakeSigningMethod(
        override val type: SigningMethodType,
        val signResult: Result<String>
    ) : SigningMethod {
        override suspend fun sign(challenge: String): Result<String> = signResult
    }

    private class FakeSigningMethodsRepository(
        var result: Result<List<SigningMethod>>
    ) : SigningMethodsRepository {
        override suspend fun getSigningMethods(): Result<List<SigningMethod>> = result
    }
}
