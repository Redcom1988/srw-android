package com.redcom1988.domain.auth.interactor

import com.redcom1988.domain.auth.repository.AuthRepository
import com.redcom1988.domain.preference.ApplicationPreference

class Logout(
    private val authRepository: AuthRepository,
    private val preference: ApplicationPreference
) {
    suspend fun await(): Result {
        return try {
            val refreshToken = preference.refreshToken().get()

            if (refreshToken.isNotEmpty()) {
                authRepository.logout(refreshToken)
            }

            preference.accessToken().delete()
            preference.refreshToken().delete()

            Result.Success
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    sealed interface Result {
        data object Success : Result
        data class Error(val error: Throwable) : Result
    }

}