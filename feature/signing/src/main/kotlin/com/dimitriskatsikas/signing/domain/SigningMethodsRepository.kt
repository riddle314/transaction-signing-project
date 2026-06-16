package com.dimitriskatsikas.signing.domain

interface SigningMethodsRepository {

    suspend fun getSigningMethods(): Result<List<SigningMethod>>
}
