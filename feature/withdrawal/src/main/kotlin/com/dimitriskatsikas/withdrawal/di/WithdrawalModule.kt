package com.dimitriskatsikas.withdrawal.di

import com.dimitriskatsikas.withdrawal.data.WithdrawalTransactionRepositoryImpl
import com.dimitriskatsikas.withdrawal.domain.WithdrawalTransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class WithdrawalModule {

    @Binds
    abstract fun bindWithdrawalTransactionRepository(
        impl: WithdrawalTransactionRepositoryImpl
    ): WithdrawalTransactionRepository
}
