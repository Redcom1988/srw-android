package com.redcom1988.domain.auth.interactor

import com.redcom1988.domain.auth.model.AuthToken
import com.redcom1988.domain.auth.repository.AuthRepository
import com.redcom1988.domain.preference.ApplicationPreference

class Login(
    private val authRepository: AuthRepository,
    private val preference: ApplicationPreference
) {

    suspend fun await(nfcNumber: String): Result {
        return try {
            val authToken = authRepository.login(nfcNumber)

            preference.accessToken().set(authToken.accessToken)
            preference.refreshToken().set(authToken.refreshToken)

            Result.Success(authToken)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    sealed interface Result {
        data class Success(val authToken: AuthToken) : Result
        data class Error(val error: Throwable) : Result
    }

}