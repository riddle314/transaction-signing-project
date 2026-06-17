package com.dimitriskatsikas.withdrawal.data

import com.dimitriskatsikas.withdrawal.domain.TransactionQuotation
import com.dimitriskatsikas.withdrawal.domain.WithdrawalTransactionRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

internal class WithdrawalTransactionRepositoryImpl @Inject constructor() : WithdrawalTransactionRepository {
    override suspend fun getQuotation(amount: String): Result<TransactionQuotation> {
        delay(1500.milliseconds)
        return Result.success(
            TransactionQuotation(
                id = "1213412431",
                amount = amount,
                fee = "1",
                challenge = "challenge",
                expiresAt = 12023231L
            )
        )
    }

    override suspend fun submitSignedTransaction(signature: String): Result<Unit> {
        delay(1500.milliseconds)
        return Result.success(Unit)
    }

}
