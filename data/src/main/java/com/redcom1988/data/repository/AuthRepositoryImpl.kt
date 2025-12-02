package com.redcom1988.data.repository

import com.redcom1988.data.remote.SRWApi
import com.redcom1988.domain.auth.model.AuthToken
import com.redcom1988.domain.auth.repository.AuthRepository

class AuthRepositoryImpl(
    private val api: SRWApi
) : AuthRepository {

    override suspend fun login(nfcNumber: String): AuthToken {
        val response = api.login(nfcNumber)

        if (response.error != null) {
            throw Exception(response.error)
        }

        if (response.success != true) {
            throw Exception("Login failed: ${response.message ?: "Unknown error"}")
        }

        val data = response.data ?: throw Exception("No data received")
        return AuthToken(
            accessToken = data.accessToken,
            refreshToken = data.refreshToken
        )
    }

    override suspend fun logout(refreshToken: String) {
        val response = api.logout(refreshToken)

        if (response.error != null) {
            throw Exception(response.error)
        }

        if (response.success != true) {
            throw Exception("Logout failed: ${response.message ?: "Unknown error"}")
        }

    }

    override suspend fun refreshToken(refreshToken: String): AuthToken {
        val response = api.refreshToken(refreshToken)

        if (response.error != null) {
            throw Exception(response.error)
        }

        if (response.success != true) {
            throw Exception("Login failed: ${response.message ?: "Unknown error"}")
        }

        val data = response.data ?: throw Exception("No data received")
        return AuthToken(
            accessToken = data.accessToken,
            refreshToken = data.refreshToken
        )
    }
}