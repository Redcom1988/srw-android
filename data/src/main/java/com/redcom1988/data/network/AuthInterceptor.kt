package com.redcom1988.data.network

import com.redcom1988.domain.auth.repository.AuthRepository
import com.redcom1988.domain.preference.ApplicationPreference
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that handles token refresh when API returns 401 or token expiration errors.
 *
 * This interceptor:
 * 1. Checks if response indicates token expiration (401 or error message)
 * 2. Attempts to refresh the token using the refresh token
 * 3. Retries the original request with the new token
 * 4. If refresh fails, clears tokens (forcing re-login)
 */
class AuthInterceptor(
    private val preference: ApplicationPreference,
    private val authRepository: Lazy<AuthRepository>
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Proceed with the original request
        val response = chain.proceed(originalRequest)

        // Check if we need to refresh the token
        if (shouldRefreshToken(response)) {
            response.close()

            // Try to refresh the token
            val refreshed = runBlocking {
                try {
                    val refreshToken = preference.refreshToken().get()
                    if (refreshToken.isEmpty()) {
                        return@runBlocking false
                    }

                    val authToken = authRepository.value.refreshToken(refreshToken)
                    preference.accessToken().set(authToken.accessToken)
                    preference.refreshToken().set(authToken.refreshToken)
                    true
                } catch (e: Exception) {
                    // Refresh failed, clear tokens to force re-login
                    preference.accessToken().set("")
                    preference.refreshToken().set("")
                    false
                }
            }

            if (refreshed) {
                // Retry the original request with the new token
                val newAccessToken = preference.accessToken().get()
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()

                return chain.proceed(newRequest)
            }
        }

        return response
    }

    private fun shouldRefreshToken(response: Response): Boolean {
        // Check for 401 Unauthorized
        if (response.code == 401) {
            return true
        }

        // Check for token expiration error message in response body
        try {
            val responseBody = response.peekBody(Long.MAX_VALUE).string()
            if (responseBody.contains("token is not valid or has expired", ignoreCase = true) ||
                responseBody.contains("token expired", ignoreCase = true) ||
                responseBody.contains("\"error\"", ignoreCase = true)) {

                // Parse JSON to check if it's an auth error
                val jsonElement = Json.parseToJsonElement(responseBody)
                val errorMessage = jsonElement.jsonObject["error"]?.jsonPrimitive?.content
                if (errorMessage != null &&
                    (errorMessage.contains("token", ignoreCase = true) ||
                     errorMessage.contains("expired", ignoreCase = true) ||
                     errorMessage.contains("unauthorized", ignoreCase = true))) {
                    return true
                }
            }
        } catch (e: Exception) {
            // If we can't parse the response, don't trigger refresh
            return false
        }

        return false
    }
}

