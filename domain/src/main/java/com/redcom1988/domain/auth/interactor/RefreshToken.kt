package com.redcom1988.domain.auth.interactor

import com.redcom1988.domain.auth.model.AuthToken
import com.redcom1988.domain.auth.repository.AuthRepository
import com.redcom1988.domain.preference.ApplicationPreference

class RefreshToken(
    private val authRepository: AuthRepository,
    private val preference: ApplicationPreference
) {

    suspend fun await(): Result {
        return try {
            val refreshToken = preference.refreshToken().get()

            if (refreshToken.isEmpty()) {
                return Result.Error(Exception("No refresh token available"))
            }

            val authToken = authRepository.refreshToken(refreshToken)

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