package com.dimitriskatsikas.withdrawal.domain

data class TransactionQuotation(
    val id: String,
    val amount: String,
    val fee: String,
    val challenge: String,
    val expiresAt: Long
)
