package com.redcom1988.domain.preference

import com.redcom1988.core.preference.PreferenceStore
import com.redcom1988.core.preference.getEnum
import com.redcom1988.domain.theme.Themes

class ApplicationPreference(
    private val preferenceStore: PreferenceStore
) {
    fun testPreference() = preferenceStore.getBoolean(
        key = "test_preference",
        defaultValue = false
    )

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
        defaultValue = "srw-api.achmad.dev"
    )

    fun appTheme() = preferenceStore.getEnum(
        key = "app_theme",
        defaultValue = Themes.SYSTEM,
    )
}