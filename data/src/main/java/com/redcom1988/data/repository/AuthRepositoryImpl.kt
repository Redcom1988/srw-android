package com.redcom1988.data.repository

import com.redcom1988.core.network.parseAs
import com.redcom1988.data.remote.SRWApi
import com.redcom1988.data.remote.model.BaseResponse
import com.redcom1988.data.remote.model.auth.AuthResponse
import com.redcom1988.domain.auth.model.AuthToken
import com.redcom1988.domain.auth.repository.AuthRepository

class AuthRepositoryImpl(
    private val api: SRWApi
) : AuthRepository {

    override suspend fun login(nfcNumber: String): AuthToken {
        val response = api.login(nfcNumber)

        val data = response.parseAs<BaseResponse<AuthResponse>>()

        if (data.success == false) {
            throw Exception("Invalid login credentials")
        }

        val authData = data.data ?: throw Exception("No data received")
        return AuthToken(
            accessToken = authData.accessToken,
            refreshToken = authData.refreshToken
        )
    }

    override suspend fun logout(refreshToken: String) {
        val response = api.logout(refreshToken)

        val data = response.parseAs<BaseResponse<String?>>()

        if (data.success == false) {
            throw Exception(data.message ?: "Logout failed")
        }
    }

    override suspend fun refreshToken(refreshToken: String): AuthToken {
        val response = api.refreshToken(refreshToken)

        val data = response.parseAs<BaseResponse<AuthResponse>>()

        if (data.success == false) {
            throw Exception(data.message ?: "Token refresh failed")
        }

        val authData = data.data ?: throw Exception("No data received")
        return AuthToken(
            accessToken = authData.accessToken,
            refreshToken = authData.refreshToken
        )
    }
}