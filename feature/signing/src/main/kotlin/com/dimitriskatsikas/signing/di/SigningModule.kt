package com.dimitriskatsikas.signing.di

import com.dimitriskatsikas.signing.data.SigningMethodsRepositoryImpl
import com.dimitriskatsikas.signing.domain.SigningMethodsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SigningModule {

    @Binds
    abstract fun bindSigningMethodsRepository(
        impl: SigningMethodsRepositoryImpl
    ): SigningMethodsRepository
}
