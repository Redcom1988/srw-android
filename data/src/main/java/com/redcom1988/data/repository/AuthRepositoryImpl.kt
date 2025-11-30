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
}