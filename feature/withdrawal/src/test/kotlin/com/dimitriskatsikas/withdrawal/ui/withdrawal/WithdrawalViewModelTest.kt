package com.dimitriskatsikas.withdrawal.ui.withdrawal

import app.cash.turbine.test
import com.dimitriskatsikas.common.dispatchers.AppDispatchers
import com.dimitriskatsikas.navigation.OperationType
import com.dimitriskatsikas.signing.domain.SigningCoordinator
import com.dimitriskatsikas.signing.domain.SigningResult
import com.dimitriskatsikas.withdrawal.domain.TransactionQuotation
import com.dimitriskatsikas.withdrawal.domain.WithdrawalTransactionRepository
import com.dimitriskatsikas.withdrawal.ui.withdrawal.WithdrawalView.CtaState
import com.dimitriskatsikas.withdrawal.ui.withdrawal.WithdrawalView.Effect
import com.dimitriskatsikas.withdrawal.ui.withdrawal.WithdrawalView.ErrorType
import com.dimitriskatsikas.withdrawal.ui.withdrawal.WithdrawalView.State
import com.dimitriskatsikas.withdrawal.ui.withdrawal.WithdrawalView.UiAction
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

@OptIn(ExperimentalCoroutinesApi::class)
class WithdrawalViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val appDispatchers = object : AppDispatchers {
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
        override val main: CoroutineDispatcher = testDispatcher
    }

    private val signingCoordinator = SigningCoordinator()
    private lateinit var fakeRepository: FakeWithdrawalTransactionRepository
    private lateinit var testedClass: WithdrawalViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeWithdrawalTransactionRepository()
        testedClass = WithdrawalViewModel(
            withdrawalTransactionRepository = fakeRepository,
            signingCoordinator = signingCoordinator,
            appDispatchers = appDispatchers
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when ViewModel initializes, then initial state is empty content with disabled CTA`() = runTest {
        testedClass.state.test {
            assertEquals(State.Content(), awaitItem())
        }
    }

    @Test
    fun `when AmountChanged action received, then updates state and CTA enablement`() = runTest {
        testedClass.state.test {
            assertEquals(State.Content(), awaitItem())

            // Change to non-empty
            testedClass.onUiAction(UiAction.AmountChanged("100"))
            assertEquals(State.Content("100", CtaState.Enabled), awaitItem())

            // Change back to empty
            testedClass.onUiAction(UiAction.AmountChanged(""))
            assertEquals(State.Content("", CtaState.Disabled), awaitItem())
        }
    }

    @Test
    fun `given successful quotation and signing, when SubmitWithdrawal action received, then successfully submits transaction and transitions state to Success`() = runTest {
        fakeRepository.apply {
            getQuotationResult = Result.success(
                TransactionQuotation("id1", "100", "1", "challenge_123", 0L)
            )
            submitSignedTransactionResult = Result.success(Unit)
        }

        testedClass.state.test {
            val stateTurbine = this
            testedClass.effect.test {
                val effectTurbine = this
                assertEquals(State.Content(), stateTurbine.awaitItem())

                testedClass.onUiAction(UiAction.AmountChanged("100"))
                assertEquals(State.Content("100", CtaState.Enabled), stateTurbine.awaitItem())

                // Trigger submission - sequential due to UnconfinedTestDispatcher
                testedClass.onUiAction(UiAction.SubmitWithdrawal)

                // Loading state
                assertEquals(State.Content("100", CtaState.Loading), stateTurbine.awaitItem())

                // Navigate effect
                assertEquals(
                    Effect.NavigateToSigning(OperationType.WITHDRAWAL, "challenge_123"),
                    effectTurbine.awaitItem()
                )

                // Resume via signingCoordinator
                signingCoordinator.sendResult("challenge_123", SigningResult.Success("signature_abc"))

                // Final state
                assertEquals(State.Success, stateTurbine.awaitItem())
            }
        }

        assertEquals("signature_abc", fakeRepository.submittedSignature)
    }

    @Test
    fun `given successful quotation and canceled signing, when SubmitWithdrawal action received, then handles cancellation and reverts state`() = runTest {
        fakeRepository.getQuotationResult = Result.success(
            TransactionQuotation("id1", "100", "1", "challenge_123", 0L)
        )

        testedClass.state.test {
            val stateTurbine = this
            testedClass.effect.test {
                val effectTurbine = this
                assertEquals(State.Content(), stateTurbine.awaitItem())

                testedClass.onUiAction(UiAction.AmountChanged("100"))
                assertEquals(State.Content("100", CtaState.Enabled), stateTurbine.awaitItem())

                testedClass.onUiAction(UiAction.SubmitWithdrawal)
                assertEquals(State.Content("100", CtaState.Loading), stateTurbine.awaitItem())
                effectTurbine.awaitItem() // Skip NavigateToSigning

                signingCoordinator.sendResult("challenge_123", SigningResult.Canceled)

                assertEquals(Effect.ShowErrorToast(ErrorType.TransactionCanceled), effectTurbine.awaitItem())
                assertEquals(State.Content("100", CtaState.Enabled), stateTurbine.awaitItem())
            }
        }
    }

    @Test
    fun `given successful quotation and failed signing, when SubmitWithdrawal action received, then handles failure and reverts state`() = runTest {
        fakeRepository.getQuotationResult = Result.success(
            TransactionQuotation("id1", "100", "1", "challenge_123", 0L)
        )

        testedClass.state.test {
            val stateTurbine = this
            testedClass.effect.test {
                val effectTurbine = this
                assertEquals(State.Content(), stateTurbine.awaitItem())

                testedClass.onUiAction(UiAction.AmountChanged("100"))
                assertEquals(State.Content("100", CtaState.Enabled), stateTurbine.awaitItem())

                testedClass.onUiAction(UiAction.SubmitWithdrawal)
                assertEquals(State.Content("100", CtaState.Loading), stateTurbine.awaitItem())
                effectTurbine.awaitItem() // Skip NavigateToSigning

                signingCoordinator.sendResult("challenge_123", SigningResult.Failed)

                assertEquals(Effect.ShowErrorToast(ErrorType.TransactionFailed), effectTurbine.awaitItem())
                assertEquals(State.Content("100", CtaState.Enabled), stateTurbine.awaitItem())
            }
        }
    }

    @Test
    fun `given failed quotation request, when SubmitWithdrawal action received, then handles failure and reverts state`() = runTest {
        fakeRepository.getQuotationResult = Result.failure(Exception("Network error"))

        testedClass.state.test {
            val stateTurbine = this
            testedClass.effect.test {
                val effectTurbine = this
                assertEquals(State.Content(), stateTurbine.awaitItem())

                testedClass.onUiAction(UiAction.AmountChanged("100"))
                assertEquals(State.Content("100", CtaState.Enabled), stateTurbine.awaitItem())

                testedClass.onUiAction(UiAction.SubmitWithdrawal)

                assertEquals(State.Content("100", CtaState.Loading), stateTurbine.awaitItem())
                assertEquals(Effect.ShowErrorToast(ErrorType.TransactionFailed), effectTurbine.awaitItem())
                assertEquals(State.Content("100", CtaState.Enabled), stateTurbine.awaitItem())
            }
        }
    }

    private class FakeWithdrawalTransactionRepository : WithdrawalTransactionRepository {
        var getQuotationResult: Result<TransactionQuotation> = Result.failure(Exception("Not set"))
        var submitSignedTransactionResult: Result<Unit> = Result.failure(Exception("Not set"))
        var submittedSignature: String? = null

        override suspend fun getQuotation(amount: String): Result<TransactionQuotation> = getQuotationResult

        override suspend fun submitSignedTransaction(signature: String): Result<Unit> {
            submittedSignature = signature
            return submitSignedTransactionResult
        }
    }
}
