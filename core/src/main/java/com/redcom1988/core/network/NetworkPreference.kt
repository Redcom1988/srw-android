package com.redcom1988.core.network

import com.redcom1988.core.preference.PreferenceStore

class NetworkPreference (
    private val preferenceStore: PreferenceStore
) {
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