package com.redcom1988.data.repository

import com.redcom1988.data.remote.SRWApi
import com.redcom1988.domain.auth.model.AuthToken
import com.redcom1988.domain.auth.repository.AuthRepository

class AuthRepositoryImpl(
    private val api: SRWApi
) : AuthRepository {

    override suspend fun login(nfcNumber: String): AuthToken {
        val response = api.login(nfcNumber)

        if (!response.success) {
            throw Exception("Login failed: ${response.message}")
        }

        return AuthToken(
            accessToken = response.data.accessToken,
            refreshToken = response.data.refreshToken
        )
    }

    override suspend fun logout(refreshToken: String) {
        val response = api.logout(refreshToken)

        if (!response.success) {
            throw Exception("Logout failed: ${response.message}")
        }

    }

    override suspend fun refreshToken(refreshToken: String): AuthToken {
        val response = api.refreshToken(refreshToken)

        if (!response.success) {
            throw Exception("Login failed: ${response.message}")
        }

        return AuthToken(
            accessToken = response.data.accessToken,
            refreshToken = response.data.refreshToken
        )
    }
}