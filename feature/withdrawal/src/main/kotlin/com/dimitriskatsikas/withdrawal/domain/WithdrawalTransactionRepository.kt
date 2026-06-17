package com.dimitriskatsikas.withdrawal.domain

interface WithdrawalTransactionRepository {

    suspend fun getQuotation(amount: String): Result<TransactionQuotation>

    suspend fun submitSignedTransaction(signature: String): Result<Unit>
}
