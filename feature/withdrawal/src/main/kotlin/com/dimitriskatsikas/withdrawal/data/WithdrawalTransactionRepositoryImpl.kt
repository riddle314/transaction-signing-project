package com.dimitriskatsikas.withdrawal.data

import com.dimitriskatsikas.withdrawal.domain.TransactionQuotation
import com.dimitriskatsikas.withdrawal.domain.WithdrawalTransactionRepository
import javax.inject.Inject

class WithdrawalTransactionRepositoryImpl @Inject constructor() : WithdrawalTransactionRepository {
    override suspend fun getQuotation(amount: String): Result<TransactionQuotation> {
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
        return Result.success(Unit)
    }

}
