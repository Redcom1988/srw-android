package com.redcom1988.data.remote

import com.redcom1988.core.network.NetworkHelper
import com.redcom1988.core.network.POST
import com.redcom1988.core.network.await
import com.redcom1988.data.remote.model.BaseResponse
import com.redcom1988.data.remote.model.auth.AuthRequest
import com.redcom1988.data.remote.model.auth.AuthResponse
import com.redcom1988.domain.preference.ApplicationPreference
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class SRWApi(
    private val networkHelper: NetworkHelper,
    private val preference: ApplicationPreference
) {
    suspend fun login(nfcNumber: String): BaseResponse<AuthResponse> {
        val requestBody = Json.encodeToString(AuthRequest(nfc = nfcNumber))
            .toRequestBody("application/json".toMediaType())

        val response = networkHelper.client.newCall(
            POST(
                url = preference.baseUrl().get() + "/auth/login/client",
                body = requestBody
            )
        ).await()

        return Json.decodeFromString(response.body.string())
    }

    suspend fun refreshToken(refreshToken: String): BaseResponse<AuthResponse> {
        val requestBody = Json.encodeToString(AuthRequest(refreshToken))
            .toRequestBody("application/json".toMediaType())

        val response = networkHelper.client.newCall(
            POST(
                url = preference.baseUrl().get() + "/auth/refresh",
                body = requestBody
            )
        ).await()

        return Json.decodeFromString(response.body.string())

    }

    suspend fun logout(refreshToken: String): BaseResponse<String> {
        val requestBody = Json.encodeToString(AuthRequest(refreshToken))
            .toRequestBody("application/json".toMediaType())

        val response = networkHelper.client.newCall(
            POST(
                url = preference.baseUrl().get() + "/auth/logout",
                body = requestBody
            )
        ).await()

        return Json.decodeFromString(response.body.string())

    }
}