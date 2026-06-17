package com.dimitriskatsikas.signing.domain

internal interface SigningMethodsRepository {

    suspend fun getSigningMethods(): Result<List<SigningMethod>>
}
