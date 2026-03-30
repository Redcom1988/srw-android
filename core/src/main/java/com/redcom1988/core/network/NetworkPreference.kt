package com.redcom1988.core.network

import com.redcom1988.core.preference.PreferenceStore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class AuthEvent {
    data object TokenRefreshFailed : AuthEvent()
}

class NetworkPreference (
    private val preferenceStore: PreferenceStore
) {
    private val _authEvents = MutableSharedFlow<AuthEvent>()
    val authEvents = _authEvents.asSharedFlow()

    suspend fun emitAuthEvent(event: AuthEvent) {
        _authEvents.emit(event)
    }
    fun accessToken() = preferenceStore.getString(
        key = "access_token",
        defaultValue = ""
    )

    fun refreshToken() = preferenceStore.getString(
        key = "refresh_token",
        defaultValue = ""
    )

    fun baseUrl() = preferenceStore.getString(
        key = "api_base_url",
        defaultValue = "https://srw-api.achmad.dev"
    )

    fun onboardingComplete() = preferenceStore.getBoolean(
        key = "onboarding_complete",
        defaultValue = false
    )
}