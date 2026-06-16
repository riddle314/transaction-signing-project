package com.dimitriskatsikas.signing.data

import com.dimitriskatsikas.signing.domain.SigningMethod
import com.dimitriskatsikas.signing.domain.SigningMethodsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SigningMethodsRepositoryImpl @Inject constructor() : SigningMethodsRepository {
    override suspend fun getSigningMethods(): Result<List<SigningMethod>> {
        return Result.success(
            listOf(
                MockSigningMethod("PASSKEYS"),
                MockSigningMethod("OTP"),
                MockSigningMethod("EOA")
            )
        )
    }
}
