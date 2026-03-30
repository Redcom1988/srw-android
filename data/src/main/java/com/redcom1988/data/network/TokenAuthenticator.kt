package com.redcom1988.data.network

import com.redcom1988.core.network.AuthEvent
import com.redcom1988.core.network.NetworkHelper
import com.redcom1988.core.network.NetworkPreference
import com.redcom1988.core.network.POST
import com.redcom1988.core.network.await
import com.redcom1988.core.network.json
import com.redcom1988.core.network.parseAs
import com.redcom1988.data.remote.model.BaseResponse
import com.redcom1988.data.remote.model.auth.AuthRequest
import com.redcom1988.data.remote.model.auth.AuthResponse
import com.redcom1988.domain.auth.model.AuthToken
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val networkHelper: NetworkHelper,
    private val preference: NetworkPreference,
): Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        synchronized(this) {
            val currentAccessToken = preference.accessToken().get()
            val currentRefreshToken = preference.refreshToken().get()

            // If no refresh token available, cannot refresh
            if (currentRefreshToken.isEmpty()) {
                return null
            }

            // If the access token changed since the first failed request, retry with new token
            if (currentAccessToken != response.request.header("Authorization")?.removePrefix("Bearer ")) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentAccessToken")
                    .build()
            }

            // Fetch new tokens synchronously using runBlocking
            return try {
                runBlocking {
                    val refreshToken = preference.refreshToken().get()

                    if (refreshToken.isEmpty()) {
                        return@runBlocking null
                    }

                    val authToken = refreshToken(networkHelper.client, refreshToken)

                    preference.accessToken().set(authToken.accessToken)
                    preference.refreshToken().set(authToken.refreshToken)

                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${authToken.accessToken}")
                        .build()

                }
            } catch (_: Exception) {
                // Exception during refresh, emit event and return null
                runBlocking {
                    preference.emitAuthEvent(AuthEvent.TokenRefreshFailed)
                }
                null
            }
        }
    }

    private suspend fun refreshToken(client: OkHttpClient, token: String): AuthToken {
        val requestBody = json.encodeToString(AuthRequest(refreshToken = token))
            .toRequestBody("application/json".toMediaType())
        val response = client.newCall(
            POST(
                url = preference.baseUrl().get() + "/auth/refresh",
                body = requestBody
            )
        ).await()
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