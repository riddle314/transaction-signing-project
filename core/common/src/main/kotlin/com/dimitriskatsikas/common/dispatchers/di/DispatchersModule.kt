package com.dimitriskatsikas.common.dispatchers.di

import com.dimitriskatsikas.common.dispatchers.AppDispatchers
import com.dimitriskatsikas.common.dispatchers.AppDispatchersImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DispatchersModule {

    @Binds
    @Singleton
    abstract fun bindAppDispatchers(
        impl: AppDispatchersImpl
    ): AppDispatchers
}
