package com.redcom1988.domain.auth.interactor

import com.redcom1988.core.network.NetworkPreference
import com.redcom1988.domain.auth.model.AuthToken
import com.redcom1988.domain.auth.repository.AuthRepository
import java.io.IOException

class Login(
    private val authRepository: AuthRepository,
    private val preference: NetworkPreference
) {

    suspend fun await(nfcNumber: String): Result {
        return try {
            val authToken = authRepository.login(nfcNumber)
            preference.accessToken().set(authToken.accessToken)
            preference.refreshToken().set(authToken.refreshToken)
            Result.Success(authToken)
        } catch (e: IOException) {
            Result.Error("Unable to connect. Please check your network connection.")
        } catch (e: Exception) {
            Result.Error(e.message ?: "Login failed")
        }
    }

    sealed interface Result {
        data class Success(val authToken: AuthToken) : Result
        data class Error(val message: String) : Result
    }

}